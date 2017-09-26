package com.github.droibit.messenger.sample.model;

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


public class ConfirmMessageReceiver implements MessageReceiver {

    public static final String PATH_ERROR_MESSAGE = "/error_message";
    public static final String PATH_SUCCESS_MESSAGE = "/success_message";

    private final Activity activity;
    private final String path;

    public ConfirmMessageReceiver(Activity activity, String path) {
        this.activity = activity;
        this.path = path;
    }

    @Override
    public void onMessageReceived(@NonNull Messenger messenger, @Nullable String data) {
        final Intent intent = new Intent(activity, ConfirmationActivity.class)
                .putExtra(EXTRA_ANIMATION_TYPE, getAnimationType());
        if (!TextUtils.isEmpty(data)) {
            intent.putExtra(EXTRA_MESSAGE, data);
        }
        activity.startActivity(intent);
    }

    @NonNull
    @Override
    public String getPath() {
        return path;
    }

    private int getAnimationType() {
        switch (path) {
            case PATH_SUCCESS_MESSAGE:
                return SUCCESS_ANIMATION;
            case PATH_ERROR_MESSAGE:
                return FAILURE_ANIMATION;
            default:
                throw new IllegalStateException();
        }
    }
}
