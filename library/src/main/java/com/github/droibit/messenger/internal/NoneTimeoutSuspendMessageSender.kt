package com.github.droibit.messenger.internal

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.NodeApi
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

internal class NoneTimeoutSuspendMessageSender(private val apiClient: GoogleApiClient) : SuspendMessageSender {
    suspend override fun getConnectedNodes(): NodeApi.GetConnectedNodesResult {
        return suspendCancellableCoroutine { context ->
            Wearable.NodeApi.getConnectedNodes(apiClient)
                    .setResultCallback {
                        context.resume(it)
                    }
        }
    }

    suspend override fun sendMessage(nodeId: String, path: String, data: String?): MessageApi.SendMessageResult {
        return suspendCancellableCoroutine { context ->
            Wearable.MessageApi.sendMessage(apiClient, nodeId, path, data?.toByteArray(charset = Charsets.UTF_8))
                    .setResultCallback {
                        context.tryResume(it)
                    }
        }
    }
}