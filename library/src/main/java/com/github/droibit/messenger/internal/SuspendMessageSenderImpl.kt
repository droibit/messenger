package com.github.droibit.messenger.internal

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.NodeApi
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit

internal class SuspendMessageSenderImpl(
        private val apiClient: GoogleApiClient,
        private val connectNodesTimeoutMillis: Long,
        private val sendMessageTimeoutMillis: Long) : SuspendMessageSender {

    suspend override fun getConnectedNodes(): NodeApi.GetConnectedNodesResult {
        return suspendCancellableCoroutine { context ->
            Wearable.NodeApi.getConnectedNodes(apiClient)
                    .setResultCallback(
                            { context.resume(it) },
                            connectNodesTimeoutMillis, TimeUnit.MILLISECONDS
                    )
        }
    }

    suspend override fun sendMessage(nodeId: String, path: String, data: ByteArray?): MessageApi.SendMessageResult {
        return suspendCancellableCoroutine { context ->
            Wearable.MessageApi.sendMessage(apiClient, nodeId, path, data)
                    .setResultCallback(
                            { context.tryResume(it) },
                            sendMessageTimeoutMillis, TimeUnit.MILLISECONDS
                    )
        }
    }
}