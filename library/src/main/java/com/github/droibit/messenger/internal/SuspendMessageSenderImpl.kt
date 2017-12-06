package com.github.droibit.messenger.internal

import android.support.annotation.VisibleForTesting
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageApi.MessageListener
import com.google.android.gms.wearable.NodeApi
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.suspendCoroutine

@VisibleForTesting
internal const val ADD_LISTENER_TIMEOUT_MILLIS = 1_000L

internal class SuspendMessageSenderImpl(
        private val apiClient: GoogleApiClient,
        private val nodeApi: NodeApi,
        private val messageApi: MessageApi,
        private val getConnectNodesTimeoutMillis: Long,
        private val sendMessageTimeoutMillis: Long) : SuspendMessageSender {

    suspend override fun getConnectedNodes(): NodeApi.GetConnectedNodesResult {
        return suspendCancellableCoroutine { context ->
            val getConnectedNodesResult = nodeApi.getConnectedNodes(apiClient).also {
                it.setResultCallback(
                        { context.resume(it) },
                        getConnectNodesTimeoutMillis, TimeUnit.MILLISECONDS
                )
            }

            context.invokeOnCompletion(onCancelling = true) {
                if (!getConnectedNodesResult.isCanceled) getConnectedNodesResult.cancel()
            }
        }
    }

    suspend override fun sendMessage(nodeId: String, path: String,
            data: ByteArray?): MessageApi.SendMessageResult {
        return suspendCancellableCoroutine { context ->
            val sendMessageResult = messageApi.sendMessage(apiClient, nodeId, path, data).also {
                it.setResultCallback(
                        { context.resume(it) },
                        sendMessageTimeoutMillis, TimeUnit.MILLISECONDS
                )
            }

            context.invokeOnCompletion(onCancelling = true) {
                if (!sendMessageResult.isCanceled) sendMessageResult.cancel()
            }
        }
    }

    suspend override fun addListener(listener: MessageListener): Status {
        return suspendCancellableCoroutine { context ->
            val addListenerResult = messageApi.addListener(apiClient, listener).apply {
                setResultCallback(
                        { context.resume(it) },
                        ADD_LISTENER_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS
                )
            }

            context.invokeOnCompletion(onCancelling = true) {
                if (!addListenerResult.isCanceled) addListenerResult.cancel()
            }
        }
    }

    suspend override fun removeListener(listener: MessageListener): Status {
        return suspendCoroutine { context ->
            messageApi.removeListener(apiClient, listener).apply {
                setResultCallback { context.resume(it) }
            }
        }
    }
}