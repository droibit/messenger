package com.github.droibit.messenger.sample.model

import android.app.Activity
import android.content.Intent
import android.support.wearable.activity.ConfirmationActivity
import android.support.wearable.activity.ConfirmationActivity.*
import android.util.Log
import com.github.droibit.messenger.MessageHandler
import com.github.droibit.messenger.Messenger


class ConfirmMessageHandler(
        private val activity: Activity,
        override val path: String) : MessageHandler {

    private val animationType: Int
        get() {
            return when (path) {
                PATH_SUCCESS_MESSAGE -> SUCCESS_ANIMATION
                PATH_ERROR_MESSAGE -> FAILURE_ANIMATION
                else -> throw IllegalStateException()
            }
        }

    override fun onMessageReceived(messenger: Messenger, sourceNodeId: String, data: String) {
        Log.d(TAG, "#onMessageReceived(path=$path, data=$data")

        val intent = Intent(activity, ConfirmationActivity::class.java)
                .putExtra(EXTRA_ANIMATION_TYPE, animationType)
        if (data.isNotEmpty()) {
            intent.putExtra(EXTRA_MESSAGE, data)
        }
        activity.startActivity(intent)
    }

    companion object {

        val PATH_ERROR_MESSAGE = "/error_message"
        val PATH_SUCCESS_MESSAGE = "/success_message"
        private val TAG = ConfirmMessageHandler::class.java.simpleName
    }
}