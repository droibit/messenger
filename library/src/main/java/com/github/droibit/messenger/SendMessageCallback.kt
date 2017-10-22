package com.github.droibit.messenger

import com.google.android.gms.common.api.Status

/**
 * Callback of send message using the Message API.
 */
interface SendMessageCallback {

    /**
     * Called in the message send.
     *
     * @param status send result
     */
    fun onMessageResult(status: Status)
}
