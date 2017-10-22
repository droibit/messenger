package com.github.droibit.messenger.sample

import android.app.Activity
import android.util.Log
import android.widget.Toast

import com.github.droibit.messenger.MessageHandler
import com.github.droibit.messenger.Messenger

class WearMessageHandler(private val activity: Activity) : MessageHandler {

    override val path = PATH_REQUEST_MESSAGE_FROM_WEAR

    override fun onMessageReceived(messenger: Messenger, data: String) {
        Log.d(TAG, "#onMessageReceived(path=$path, data=$data")
        activity.runOnUiThread { Toast.makeText(activity, data, Toast.LENGTH_SHORT).show() }
    }

    companion object {

        private val PATH_REQUEST_MESSAGE_FROM_WEAR = "/request_message_wear"
        private val TAG = WearMessageHandler::class.java.simpleName
    }
}
