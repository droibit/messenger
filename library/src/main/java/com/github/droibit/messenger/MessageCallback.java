package com.github.droibit.messenger;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.Status;

/**
 * Callback of send message using the Message API.
 */
public interface MessageCallback {

    /**
     * Called in the message send.
     *
     * @param status send result
     */
    void onMessageResult(@NonNull Status status);
}
