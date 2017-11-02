package com.github.droibit.messenger.sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.github.droibit.messenger.MessageHandlerRegistry
import com.github.droibit.messenger.Messenger
import com.github.droibit.messenger.sample.WearMessageHandler.Companion.PATH_REQUEST_MESSAGE_FROM_WEAR
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch


class MainActivity : Activity(), ConnectionCallbacks {

    private lateinit var googleApiClient: GoogleApiClient

    private lateinit var messenger: Messenger

    private lateinit var handlers: MessageHandlerRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build()

        messenger = Messenger.Builder(googleApiClient)
                .sendMessageTimeout(5000L, 2500L)
                .build()
        handlers = MessageHandlerRegistry(messenger, hashMapOf(
                PATH_REQUEST_MESSAGE_FROM_WEAR to WearMessageHandler(this)
        ))
    }

    override fun onResume() {
        super.onResume()

        if (!googleApiClient.isConnected) {
            googleApiClient.connect()
        }
    }

    override fun onPause() {
        super.onPause()

        if (googleApiClient.isConnected) {
            Wearable.MessageApi.removeListener(googleApiClient, handlers)
            googleApiClient.disconnect()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        return if (id == R.id.action_settings) true else super.onOptionsItemSelected(item)
    }

    override fun onConnected(bundle: Bundle?) {
        Log.d(TAG, "#onConnected")
        Wearable.MessageApi.addListener(googleApiClient, handlers)
    }

    override fun onConnectionSuspended(i: Int) {}

    fun onSendMessage(v: View) {
        sendMessage(PATH_DEFAULT_MESSAGE, "Hello, world")
    }

    fun onSendErrorMessage(v: View) {
        sendMessage(PATH_ERROR_MESSAGE, "Not connected to the network")
    }

    fun onSendErrorMessage2(v: View) {
        sendMessage(PATH_ERROR_MESSAGE, "Oops")
    }

    fun onSendSuccessMessage(v: View) {
        sendMessage(PATH_SUCCESS_MESSAGE, "Authenticated")
    }

    fun onSendMessageWithReceiveMessage(v: View) {
        sendMessage(PATH_REQUEST_MESSAGE, null)
    }

    fun onReceiveMessageWithReject(v: View) = Unit

    private fun sendMessage(path: String, message: String?): Job {
        return launch {
            Log.d(TAG, "sendMessage($message, to=$path) in ${Thread.currentThread().name}.")
            val status = messenger.sendMessage(path, message?.toByteArray())
            if (status.isSuccess) {
                Log.d(TAG, "Succeed to send message in ${Thread.currentThread().name}.")
            } else {
                Log.d(TAG, "Failed send message(code=${status.statusCode}, msg=${status.statusMessage}) in ${Thread.currentThread().name}")
            }
        }
    }

    companion object {

        private val PATH_DEFAULT_MESSAGE = "/message"
        private val PATH_ERROR_MESSAGE = "/error_message"
        private val PATH_SUCCESS_MESSAGE = "/success_message"
        private val PATH_REQUEST_MESSAGE = "/request_message"

        private val TAG = MainActivity::class.java.simpleName
    }
}
