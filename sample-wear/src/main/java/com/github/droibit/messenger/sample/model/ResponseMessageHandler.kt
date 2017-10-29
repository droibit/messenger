package com.github.droibit.messenger.sample.model

import android.util.Log
import com.github.droibit.messenger.MessageHandler
import com.github.droibit.messenger.Messenger
import kotlinx.coroutines.experimental.launch

class ResponseMessageHandler : MessageHandler {

    override fun onMessageReceived(messenger: Messenger, sourceNodeId: String, data: String) {
        Log.d(TAG, "#onMessageReceived(path=$PATH_REQUEST_MESSAGE, data=$data")

        launch {
            val status = messenger.sendMessage(sourceNodeId, PATH_REQUEST_MESSAGE_FROM_WEAR,
                    "Yeah!! from Android Wear")
            if (status.isSuccess) {
                Log.d(TAG, "Succeed to send message in ${Thread.currentThread().name}.")
            } else {
                Log.d(TAG, "Failed send message(code=${status.statusCode}, msg=${status.statusMessage}) in ${Thread.currentThread().name}")
            }
        }
    }

    companion object {

        const val PATH_REQUEST_MESSAGE = "/request_message"
        const val PATH_REQUEST_MESSAGE_FROM_WEAR = "/request_message_wear"

        private val TAG = ResponseMessageHandler::class.java.simpleName
    }
}
