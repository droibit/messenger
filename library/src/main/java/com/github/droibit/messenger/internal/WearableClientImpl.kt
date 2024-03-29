package com.github.droibit.messenger.internal

import com.github.droibit.messenger.internal.WearableClient.ClientProvider
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient.OnMessageReceivedListener
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

internal class WearableClientImpl(
  private val clientProvider: ClientProvider,
  private val getNodesTimeoutMillis: Long,
  private val sendMessageTimeoutMillis: Long
) : WearableClient {

  private val nodeClient get() = clientProvider.nodeClient

  private val messageClient get() = clientProvider.messageClient

  private val capabilityClient get() = clientProvider.capabilityClient

  @ExperimentalCoroutinesApi
  override val messageEvents: Flow<MessageEvent>
    get() {
      return callbackFlow {
        var listener: OnMessageReceivedListener? = OnMessageReceivedListener {
          trySend(it)
        }
        try {
          messageClient.addListener(requireNotNull(listener)).await()
        } catch (e: ApiException) {
          listener = null
          close(e)
        }

        awaitClose {
          if (listener != null) {
            messageClient.removeListener(listener)
          }
        }
      }
    }

  @Throws(ApiException::class)
  override suspend fun getConnectedNodes(): List<Node> {
    return withTimeout(getNodesTimeoutMillis) {
      nodeClient.connectedNodes.await()
    }
  }

  @Throws(ApiException::class)
  override suspend fun getCapability(
    capability: String,
    nodeFilter: Int
  ): CapabilityInfo {
    return withTimeout(getNodesTimeoutMillis) {
      capabilityClient.getCapability(capability, nodeFilter).await()
    }
  }

  @Throws(ApiException::class)
  override suspend fun sendMessage(
    nodeId: String,
    path: String,
    data: ByteArray?
  ): Int {
    return withTimeout(sendMessageTimeoutMillis) {
      messageClient.sendMessage(nodeId, path, data).await()
    }
  }

  @Throws(ApiException::class)
  override suspend fun addListener(listener: OnMessageReceivedListener) {
    messageClient.addListener(listener).await()
  }

  @Throws(ApiException::class)
  override suspend fun removeListener(listener: OnMessageReceivedListener): Boolean {
    return messageClient.removeListener(listener).await()
  }
}

