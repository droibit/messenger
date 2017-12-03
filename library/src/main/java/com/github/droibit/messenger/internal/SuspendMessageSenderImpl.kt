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
            val status = Wearable.NodeApi.getConnectedNodes(apiClient).apply {
                setResultCallback(
                        { context.resume(it) },
                        connectNodesTimeoutMillis, TimeUnit.MILLISECONDS
                )
            }

            context.invokeOnCompletion(onCancelling = true) {
                if (!status.isCanceled) status.cancel()
            }
        }
    }

    suspend override fun sendMessage(nodeId: String, path: String, data: ByteArray?): MessageApi.SendMessageResult {
        return suspendCancellableCoroutine { context ->
            val status = Wearable.MessageApi.sendMessage(apiClient, nodeId, path, data).apply {
                setResultCallback(
                        { context.resume(it) },
                        sendMessageTimeoutMillis, TimeUnit.MILLISECONDS
                )
            }

            context.invokeOnCompletion {
                if (!status.isCanceled) status.cancel()
            }
        }
    }
}