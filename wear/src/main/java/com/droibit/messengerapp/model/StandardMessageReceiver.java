package com.droibit.messengerapp.model;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.droibit.messenger.MessageReceiver;
import com.droibit.messenger.Messenger;

/**
 * @auther kumagai
 * @since 15/05/25
 */
public class StandardMessageReceiver implements MessageReceiver {

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
}
