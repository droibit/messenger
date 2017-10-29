package com.github.droibit.messenger.sample.model

import android.app.Activity
import android.util.Log
import android.widget.Toast

import com.github.droibit.messenger.MessageHandler
import com.github.droibit.messenger.Messenger

class StandardMessageHandler(private val activity: Activity) : MessageHandler {

    override fun onMessageReceived(messenger: Messenger, sourceNodeId: String, data: String) {
        Log.d(TAG, "#onMessageReceived(path=$PATH_DEFAULT_MESSAGE, data=$data")
        activity.runOnUiThread { Toast.makeText(activity, data, Toast.LENGTH_SHORT).show() }
    }

    companion object {

        const val PATH_DEFAULT_MESSAGE = "/message"
        private val TAG = StandardMessageHandler::class.java.simpleName
    }
}
