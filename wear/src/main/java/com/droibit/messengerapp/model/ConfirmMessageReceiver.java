package com.droibit.messengerapp.model;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.activity.ConfirmationActivity;
import android.text.TextUtils;

import com.github.droibit.messenger.MessageReceiver;
import com.github.droibit.messenger.Messenger;

import static android.support.wearable.activity.ConfirmationActivity.EXTRA_ANIMATION_TYPE;
import static android.support.wearable.activity.ConfirmationActivity.EXTRA_MESSAGE;
import static android.support.wearable.activity.ConfirmationActivity.FAILURE_ANIMATION;
import static android.support.wearable.activity.ConfirmationActivity.SUCCESS_ANIMATION;


/**
 * @auther kumagai
 * @since 15/05/25
 */
public class ConfirmMessageReceiver implements MessageReceiver {

    public static final String PATH_ERROR_MESSAGE = "/error_message";
    public static final String PATH_SUCCESS_MESSAGE = "/success_message";

    private final Activity mActivity;
    private final String mPath;

    public ConfirmMessageReceiver(Activity activity, String path) {
        mActivity = activity;
        mPath = path;
    }

    /** {@inheritDoc} */
    @Override
    public void onMessageReceived(Messenger messenger, @Nullable String data) {
        final Intent intent = new Intent(mActivity, ConfirmationActivity.class)
                .putExtra(EXTRA_ANIMATION_TYPE, getAnimationType());
        if (!TextUtils.isEmpty(data)) {
            intent.putExtra(EXTRA_MESSAGE, data);
        }
        mActivity.startActivity(intent);
    }

    /** {@inheritDoc} */
    @NonNull
    @Override
    public String getPath() {
        return mPath;
    }

    private int getAnimationType() {
        switch (mPath) {
            case PATH_SUCCESS_MESSAGE:
                return SUCCESS_ANIMATION;
            case PATH_ERROR_MESSAGE:
                return FAILURE_ANIMATION;
            default:
                throw new IllegalStateException();
        }
    }
}
