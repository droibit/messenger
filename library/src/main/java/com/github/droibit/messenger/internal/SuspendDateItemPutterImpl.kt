package com.github.droibit.messenger.internal

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.DataApi.DataItemResult
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit

internal class SuspendDateItemPutterImpl(
        private val googleApiClient: GoogleApiClient,
        private val putDataItemTimeoutMillis: Long) : SuspendDateItemPutter {

    suspend override fun putDataItem(request: PutDataRequest): DataItemResult {
        return suspendCancellableCoroutine { context ->
            Wearable.DataApi.putDataItem(googleApiClient, request)
                    .setResultCallback(
                            { context.resume(it) },
                            putDataItemTimeoutMillis, TimeUnit.MILLISECONDS
                    )
        }
    }
}