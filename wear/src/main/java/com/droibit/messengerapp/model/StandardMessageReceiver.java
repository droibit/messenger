package com.droibit.messengerapp.model;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.github.droibit.messenger.MessageReceiver;
import com.github.droibit.messenger.Messenger;

/**
 * @auther kumagai
 * @since 15/05/25
 */
public class StandardMessageReceiver implements MessageReceiver {

    public static final String PATH_DEFAULT_MESSAGE = "/message";

    private final Activity mActivity;

    public StandardMessageReceiver(Activity activity) {
        mActivity = activity;
    }

    /** {@inheritDoc} */
    @Override
    public void onMessageReceived(Messenger messenger, @Nullable final String data) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity, data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** {@inheritDoc} */
    @NonNull
    @Override
    public String getPath() {
        return PATH_DEFAULT_MESSAGE;
    }
}
