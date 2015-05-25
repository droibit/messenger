package com.droibit.messengerapp.model;

import android.support.annotation.Nullable;
import android.util.Log;

import com.droibit.messenger.MessageCallback;
import com.droibit.messenger.MessageReceiver;
import com.droibit.messenger.Messenger;
import com.droibit.messengerapp.BuildConfig;
import com.droibit.messengerapp.MainActivity;
import com.google.android.gms.common.api.Status;

/**
 * @auther kumagai
 * @since 15/05/25
 */
public class ResponseMessageReceiver implements MessageReceiver {

    /** {@inheritDoc} */
    @Override
    public void onMessageReceived(Messenger messenger, @Nullable String data) {
        messenger.sendTo(MainActivity.PATH_REQUEST_MESSAGE_FROM_WEAR, "Message from Android Wear", new MessageCallback() {
            @Override
            public void onMessageResult(Status status) {
                if (status.isSuccess()) {
                    return;
                }
                Log.d(BuildConfig.BUILD_TYPE, "ERROR: " + status.getStatusCode());
            }
        });
    }
}
