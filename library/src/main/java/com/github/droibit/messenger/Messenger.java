package com.github.droibit.messenger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult;
import com.google.android.gms.wearable.Wearable;

import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.wearable.MessageApi.MessageListener;

/**
 * Class for communication between the wear and handheld using the Message API.
 * When you register a receiver, it will be automatically called back when the message is received.
 * <p>
 * Whether whether to reject the message, use the {@link RejectDecider}.
 * Receiver at the time rejected to register using the {@link #KEY_MESSAGE_REJECTED}.
 */
public class Messenger implements MessageListener {

    /**
     * Interface for determine whether to receive a message.
     * It will use in the case of a decision related to the whole
     * (e.g. Network is not connected)
     */
    public interface RejectDecider {

        /**
         * Whether receive the messages.
         *
         * @return If true do not receive the messages, if false receive the messages.
         */
        boolean shouldReject();
    }

    /**
     * The utility class that simplifies the registration of receiver.
     */
    public static class Builder {

        private final Messenger messenger;

        /**
         * Create a new instance.
         */
        public Builder(@NonNull GoogleApiClient googleApiClient) {
            messenger = new Messenger(googleApiClient);
        }

        /**
         * Register a new receiver.
         */
        public Builder register(@NonNull MessageReceiver receiver) {
            messenger.registerReceiver(receiver);
            return this;
        }

        /**
         * Set the {@link RejectDecider}.
         */
        public Builder rejectDecider(@NonNull RejectDecider rejectDecider) {
            messenger.setRejectDecider(rejectDecider);
            return this;
        }

        /**
         * Get a new instance of the Messenger.
         */
        public Messenger get() {
            return messenger;
        }
    }

    /**
     * The path of the reject receiver
     */
    public static final String KEY_MESSAGE_REJECTED = "rejected-message";

    private final Map<String, MessageReceiver> receivers;
    private final GoogleApiClient googleApiClient;
    private RejectDecider rejectDecider;

    /**
     * Create a new instance.
     */
    public Messenger(@NonNull GoogleApiClient googleApiClient) {
        receivers = new HashMap<>();
        this.googleApiClient = googleApiClient;
    }

    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {
        if (rejectDecider != null && rejectDecider.shouldReject()) {
            if (receivers.containsKey(KEY_MESSAGE_REJECTED)) {
                receivers.get(KEY_MESSAGE_REJECTED).onMessageReceived(this, null);
            }
            return;
        }

        final String path = messageEvent.getPath();
        if (!receivers.containsKey(path)) {
            throw new IllegalStateException(String.format("The callback corresponding to the path(%s) is not registered.", path));
        }

        final byte[] data = messageEvent.getData();
        final MessageReceiver receiver = receivers.get(path);
        receiver.onMessageReceived(this, data != null ? new String(data) : null);
    }

    /**
     * Sends byte[] data to path.
     *
     * @param path     specified path
     * @param data     data to be associated with the path
     * @param callback callback of send message
     */
    public void sendMessage(@NonNull final String path, @Nullable final String data, @Nullable final MessageCallback callback) {
        getConnectedNodes().setResultCallback(new ResultCallback<GetConnectedNodesResult>() {
            @Override
            public void onResult(@NonNull GetConnectedNodesResult nodesResult) {
                for (Node node : nodesResult.getNodes()) {
                    final PendingResult<SendMessageResult> messageResult = sendMessage(node.getId(), path, data);
                    if (callback == null) {
                        return;
                    }
                    messageResult.setResultCallback(new ResultCallback<SendMessageResult>() {
                        @Override
                        public void onResult(@NonNull SendMessageResult sendMessageResult) {
                            callback.onMessageResult(sendMessageResult.getStatus());
                        }
                    });
                }
            }
        });
    }

    /**
     * Register a new receiver.
     *
     * @param receiver receiver of the message
     */
    public void registerReceiver(@NonNull MessageReceiver receiver) {
        receivers.put(receiver.getPath(), receiver);
    }

    /**
     * Unregister a receiver.
     *
     * @param path specified path
     */
    public void unregisterReceiver(@NonNull String path) {
        if (receivers.containsKey(path)) {
            receivers.remove(path);
        }
    }

    /**
     * Remove all of the receivers.
     */
    public void clearReceivers() {
        receivers.clear();
    }

    /**
     * Get the {@link RejectDecider}.
     */
    public RejectDecider getRejectDecider() {
        return rejectDecider;
    }

    /**
     * Set the {@link RejectDecider}.
     * Set the null when you delete an instance.
     */
    public void setRejectDecider(@Nullable RejectDecider rejectDecider) {
        this.rejectDecider = rejectDecider;
    }

    @VisibleForTesting
    Map<String, MessageReceiver> getReceivers() {
        return receivers;
    }

    private PendingResult<GetConnectedNodesResult> getConnectedNodes() {
        return Wearable.NodeApi.getConnectedNodes(googleApiClient);
    }

    private PendingResult<SendMessageResult> sendMessage(@NonNull String nodeId, @NonNull String path, @Nullable String data) {
        return Wearable.MessageApi.sendMessage(googleApiClient,
                nodeId,
                path,
                data == null ? null : data.getBytes());
    }
}
