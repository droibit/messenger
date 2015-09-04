package com.droibit.messengerapp.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.droibit.messenger.MessageCallback;
import com.github.droibit.messenger.MessageReceiver;
import com.github.droibit.messenger.Messenger;
import com.droibit.messengerapp.BuildConfig;
import com.google.android.gms.common.api.Status;

/**
 * @auther kumagai
 * @since 15/05/25
 */
public class ResponseMessageReceiver implements MessageReceiver {

    public static final String PATH_REQUEST_MESSAGE = "/request_message";
    public static final String PATH_REQUEST_MESSAGE_FROM_WEAR = "/request_message_wear";

    /** {@inheritDoc} */
    @Override
    public void onMessageReceived(Messenger messenger, @Nullable String data) {
        messenger.sendMessage(PATH_REQUEST_MESSAGE_FROM_WEAR, "Message from Android Wear", new MessageCallback() {
            @Override
            public void onMessageResult(Status status) {
                if (status.isSuccess()) {
                    return;
                }
                Log.d(BuildConfig.BUILD_TYPE, "ERROR: " + status.getStatusCode());
            }
        });
    }

    /** {@inheritDoc} */
    @NonNull
    @Override
    public String getPath() {
        return PATH_REQUEST_MESSAGE;
    }
}
