package com.github.droibit.messenger.internal

import com.google.android.gms.common.api.GoogleApiClient

abstract class GoogleApiConnectionHandler :
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

  override fun onConnectionSuspended(cause: Int) = Unit
}