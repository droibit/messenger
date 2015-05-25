package com.droibit.messenger;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
 *
 * Whether whether to reject the message, use the {@link RejectDecider}.
 * Receiver at the time rejected to register using the {@link #KEY_MESSAGE_REJECTED}.
 *
 * @author kumagai
 * @since 2015/5/22
 */
public class Messenger implements MessageListener {

    /**
     * Interface for determine whether to receive a message.
     * It will use in the case of a decision related to the whole
     * (e.g Network is not connected)
     */
    public interface RejectDecider {

        /**
         * Whether receive the messages.
         *
         * @return If true do not receive the messages, if false receive the messages.
         */
        boolean shouldReject();
    }

    /** The path of the reject receiver */
    public static final String KEY_MESSAGE_REJECTED = "rejected-message";

    private final Map<String, MessageReceiver> mReceivers;
    private final GoogleApiClient mGoogleApiClient;
    private RejectDecider mRejectDecider;

    /**
     * Create a new instance.
     *
     * @param googleApiClient
     */
    public Messenger(GoogleApiClient googleApiClient) {
        mReceivers = new HashMap<>();
        mGoogleApiClient = googleApiClient;
    }

    /** {@inheritDoc} */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (mRejectDecider != null && mRejectDecider.shouldReject()) {
            if (mReceivers.containsKey(KEY_MESSAGE_REJECTED)) {
                mReceivers.get(KEY_MESSAGE_REJECTED).onMessageReceived(this, null);
            }
            return;
        }

        final String path = messageEvent.getPath();
        if (!mReceivers.containsKey(path)) {
            throw new IllegalStateException(String.format("The callback corresponding to the path(%s) is not registered.", path));
        }

        final byte[] data = messageEvent.getData();
        final MessageReceiver receiver = mReceivers.get(path);
        receiver.onMessageReceived(this, data != null ? new String(data) : null);
    }

    /**
     * Sends byte[] data to path.
     *
     * @param path specified path
     * @param data data to be associated with the path
     * @param callback callback of send message
     */
    public void sendTo(@NonNull final String path, @Nullable final String data, @Nullable final MessageCallback callback) {
        getConnectedNodes().setResultCallback(new ResultCallback<GetConnectedNodesResult>() {
            @Override
            public void onResult(GetConnectedNodesResult nodesResult) {
                for (Node node : nodesResult.getNodes()) {
                    final PendingResult<SendMessageResult> messageResult = sendTo(node.getId(), path, data);
                    if (callback == null) {
                        return;
                    }
                    messageResult.setResultCallback(new ResultCallback<SendMessageResult>() {
                        @Override
                        public void onResult(SendMessageResult sendMessageResult) {
                            callback.onMessageResult(sendMessageResult.getStatus());
                        }
                    });
                }
            }
        });
    }

    /**
     * Register a receiver.
     *
     * @param path specified path
     * @param receiver receiver of the message
     */
    public void registReceiver(@NonNull String path, @NonNull MessageReceiver receiver) {
        mReceivers.put(path, receiver);
    }

    /**
     * Unregister a receiver.
     *
     * @param path specified path
     */
    public void unregistReceiver(@NonNull String path) {
        if (mReceivers.containsKey(path)) {
            mReceivers.remove(path);
        }
    }

    /**
     * Remove all of the receiver.
     */
    public void clearReceivers() {
        mReceivers.clear();
    }

    /**
     * Get the {@link RejectDecider}.
     *
     * @return {@link RejectDecider} instance
     */
    public RejectDecider getRejectDecider() {
        return mRejectDecider;
    }

    /**
     * Set the {@link RejectDecider}.
     * Set the null when you delete an instance.
     *
     * @param rejectDecider
     */
    public void setRejectDecider(@Nullable RejectDecider rejectDecider) {
        mRejectDecider = rejectDecider;
    }

    Map<String, MessageReceiver> getReceivers() {
        return mReceivers;
    }

    private PendingResult<GetConnectedNodesResult> getConnectedNodes() {
        return Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
    }

    private PendingResult<SendMessageResult> sendTo(String nodeId, String path, String data) {
        return Wearable.MessageApi.sendMessage(mGoogleApiClient,
                nodeId,
                path,
                data == null ? null : data.getBytes());
    }
}
