package com.github.droibit.messenger.sample.model

import android.util.Log

import com.github.droibit.messenger.MessageCallback
import com.github.droibit.messenger.MessageHandler
import com.github.droibit.messenger.Messenger
import com.github.droibit.messenger.sample.BuildConfig
import com.google.android.gms.common.api.Status

class ResponseMessageHandler : MessageHandler {

    override val path = PATH_REQUEST_MESSAGE

    override fun onMessageReceived(messenger: Messenger, data: String) {
        messenger.sendMessage(PATH_REQUEST_MESSAGE_FROM_WEAR, "Message from Android Wear", object : MessageCallback {
            override fun onMessageResult(status: Status) {
                if (status.isSuccess) {
                    return
                }
                Log.d(BuildConfig.BUILD_TYPE, "ERROR: " + status.statusCode)
            }
        })
    }

    companion object {

        val PATH_REQUEST_MESSAGE = "/request_message"
        val PATH_REQUEST_MESSAGE_FROM_WEAR = "/request_message_wear"
    }
}
