package com.github.droibit.messenger.sample

import android.app.Activity
import android.os.Bundle

import com.github.droibit.messenger.Messenger
import com.github.droibit.messenger.sample.model.ConfirmMessageHandler
import com.github.droibit.messenger.sample.model.ResponseMessageHandler
import com.github.droibit.messenger.sample.model.StandardMessageHandler
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Wearable

import com.github.droibit.messenger.sample.model.ConfirmMessageHandler.Companion.PATH_ERROR_MESSAGE
import com.github.droibit.messenger.sample.model.ConfirmMessageHandler.Companion.PATH_SUCCESS_MESSAGE


class MainActivity : Activity(), GoogleApiClient.ConnectionCallbacks {

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
                .register(StandardMessageHandler(this))
                .register(ConfirmMessageHandler(this, PATH_SUCCESS_MESSAGE))
                .register(ConfirmMessageHandler(this, PATH_ERROR_MESSAGE))
                .register(ResponseMessageHandler())
                .rejectDecider { false }
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

    override fun onConnected(bundle: Bundle?) {
        Wearable.MessageApi.addListener(googleApiClient, messenger)
    }

    override fun onConnectionSuspended(i: Int) {
        // TODO: Implement
    }
}
