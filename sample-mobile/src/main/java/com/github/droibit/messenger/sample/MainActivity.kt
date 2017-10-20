package com.github.droibit.messenger.sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View

import com.github.droibit.messenger.MessageCallback
import com.github.droibit.messenger.Messenger
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.Wearable


class MainActivity : Activity(), ConnectionCallbacks {

    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var messenger: Messenger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        googleApiClient = GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build()

        messenger = Messenger.Builder(googleApiClient)
                .register(WearMessageHandler(this))
                .build()
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
            Wearable.MessageApi.removeListener(googleApiClient, messenger)
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
        Wearable.MessageApi.addListener(googleApiClient, messenger)
    }

    override fun onConnectionSuspended(i: Int) {}

    fun onSendMessage(v: View) {
        messenger.sendMessage(PATH_DEFAULT_MESSAGE, "Hello, world", object : MessageCallback {
            override fun onMessageResult(status: Status) {
                if (!status.isSuccess) {
                    Log.d(BuildConfig.BUILD_TYPE, "Failed send message : " + status.statusCode)
                }
            }
        })
    }

    fun onSendErrorMessage(v: View) {
        messenger.sendMessage(PATH_ERROR_MESSAGE, "Not connected to the network", null)
    }

    fun onSendErrorMessage2(v: View) {
        messenger.sendMessage(PATH_ERROR_MESSAGE, "Oops", null)
    }

    fun onSendSuccessMessage(v: View) {
        messenger.sendMessage(PATH_SUCCESS_MESSAGE, "Authenticated", null)
    }

    fun onSendMessageWithReceiveMessage(v: View) {
        messenger.sendMessage(PATH_REQUEST_MESSAGE, null, null)
    }

    fun onReceiveMessageWithReject(v: View) = Unit

    companion object {

        private val PATH_DEFAULT_MESSAGE = "/message"
        private val PATH_ERROR_MESSAGE = "/error_message"
        private val PATH_SUCCESS_MESSAGE = "/success_message"
        private val PATH_REQUEST_MESSAGE = "/request_message"
    }
}
