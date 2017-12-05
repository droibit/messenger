package com.github.droibit.messenger.internal

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageApi.MessageListener
import com.google.android.gms.wearable.NodeApi
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.suspendCoroutine

private const val ADD_MESSAGE_LISTENER_TIMEOUT_MILLIS = 1_000L

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

    suspend override fun addListener(listener: MessageListener): Status {
        return suspendCancellableCoroutine { context ->
            val status = Wearable.MessageApi.addListener(apiClient, listener).apply {
                setResultCallback { context.resume(it) }
            }

            context.invokeOnCompletion(onCancelling = true) {
                if (!status.isCanceled) status.cancel()
            }
        }
    }

    suspend override fun removeListener(listener: MessageListener): Status {
        return suspendCoroutine { context ->
            Wearable.MessageApi.removeListener(apiClient, listener).apply {
                setResultCallback { context.resume(it) }
            }
        }
    }
}