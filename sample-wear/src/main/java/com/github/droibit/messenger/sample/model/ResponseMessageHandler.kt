package com.github.droibit.messenger.sample.model

import android.util.Log
import com.github.droibit.messenger.MessageHandler
import com.github.droibit.messenger.Messenger
import kotlinx.coroutines.experimental.launch

class ResponseMessageHandler : MessageHandler {

    override val path = PATH_REQUEST_MESSAGE

    override fun onMessageReceived(messenger: Messenger, data: String) {
        Log.d(TAG, "#onMessageReceived(path=$path, data=$data")

        launch {
            val status = messenger.sendMessage(PATH_REQUEST_MESSAGE_FROM_WEAR,
                    "Message from Android Wear")
            if (status.isSuccess) {
                Log.d(TAG, "Succeed to send message in ${Thread.currentThread().name}.")
            } else {
                Log.d(TAG, "Failed send message(code=${status.statusCode}, msg=${status.statusMessage}) in ${Thread.currentThread().name}")
            }
        }
    }

    companion object {

        val PATH_REQUEST_MESSAGE = "/request_message"
        val PATH_REQUEST_MESSAGE_FROM_WEAR = "/request_message_wear"

        private val TAG = ResponseMessageHandler::class.java.simpleName
    }
}
