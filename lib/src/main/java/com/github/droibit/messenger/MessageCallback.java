package com.github.droibit.messenger;

import com.google.android.gms.common.api.Status;

/**
 * Callback of send message using the Message API.
 *
 * @auther kumagai
 * @since 15/05/23
 */
public interface MessageCallback {

    /**
     * Called in the message send.
     *
     * @param status send result
     */
    void onMessageResult(Status status);
}
