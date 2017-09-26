package com.github.droibit.messenger.sample.model;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.github.droibit.messenger.MessageReceiver;
import com.github.droibit.messenger.Messenger;

public class StandardMessageReceiver implements MessageReceiver {

    public static final String PATH_DEFAULT_MESSAGE = "/message";

    private final Activity mActivity;

    public StandardMessageReceiver(@NonNull Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onMessageReceived(@NonNull Messenger messenger, @Nullable final String data) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @NonNull
    @Override
    public String getPath() {
        return PATH_DEFAULT_MESSAGE;
    }
}
