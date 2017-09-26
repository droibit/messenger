package com.github.droibit.messenger.sample;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.github.droibit.messenger.MessageReceiver;
import com.github.droibit.messenger.Messenger;

public class WearMessageReceiver implements MessageReceiver {

    private static final String PATH_REQUEST_MESSAGE_FROM_WEAR = "/request_message_wear";

    private final Activity activity;

    public WearMessageReceiver(@NonNull Activity context) {
        activity = context;
    }

    @Override
    public void onMessageReceived(@NonNull Messenger messenger, @Nullable final String data) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public String getPath() {
        return PATH_REQUEST_MESSAGE_FROM_WEAR;
    }
}
