package com.droibit.messengerapp.model;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.wearable.activity.ConfirmationActivity;
import android.text.TextUtils;

import com.droibit.messenger.MessageReceiver;
import com.droibit.messenger.Messenger;

import static android.support.wearable.activity.ConfirmationActivity.EXTRA_ANIMATION_TYPE;
import static android.support.wearable.activity.ConfirmationActivity.EXTRA_MESSAGE;

/**
 * @auther kumagai
 * @since 15/05/25
 */
public class ConfirmMessageReceiver implements MessageReceiver {

    private final Activity mActivity;
    private final int mAnimationType;

    public ConfirmMessageReceiver(Activity activity, int animationType) {
        mActivity = activity;
        mAnimationType = animationType;
    }

    /** {@inheritDoc} */
    @Override
    public void onMessageReceived(Messenger messenger, @Nullable String data) {
        final Intent intent = new Intent(mActivity, ConfirmationActivity.class)
                .putExtra(EXTRA_ANIMATION_TYPE, mAnimationType);
        if (!TextUtils.isEmpty(data)) {
            intent.putExtra(EXTRA_MESSAGE, data);
        }
        mActivity.startActivity(intent);
    }
}
