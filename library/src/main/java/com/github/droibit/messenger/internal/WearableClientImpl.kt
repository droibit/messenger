package com.github.droibit.messenger.internal

import com.github.droibit.messenger.internal.WearableClient.ClientProvider
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient.OnMessageReceivedListener
import com.google.android.gms.wearable.Node
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import kotlin.coroutines.suspendCoroutine

internal class WearableClientImpl(
  private val clientProvider: ClientProvider,
  private val getNodesTimeoutMillis: Long,
  private val sendMessageTimeoutMillis: Long,
  private val addListenerTimeoutMills: Long
) : WearableClient {

  private val nodeClient get() = clientProvider.nodeClient

  private val messageClient get() = clientProvider.messageClient

  private val capabilityClient get() = clientProvider.capabilityClient

  @Throws(ApiException::class)
  override suspend fun getConnectedNodes(): List<Node> {
    return withTimeout(getNodesTimeoutMillis) {
      suspendCancellableCoroutine<List<Node>> { cont ->
        val listener = CompleteEventHandler(cont)
            .also {
              nodeClient.connectedNodes.addOnCompleteListener(it)
            }
        cont.invokeOnCancellation {
          listener.cancel()
        }
      }
    }
  }

  @Throws(ApiException::class)
  override suspend fun getCapability(
    capability: String,
    nodeFilter: Int
  ): CapabilityInfo {
    return withTimeout(getNodesTimeoutMillis) {
      suspendCancellableCoroutine<CapabilityInfo> { cont ->
        val listener = CompleteEventHandler(cont)
            .apply {
              capabilityClient.getCapability(capability, nodeFilter)
                  .addOnCompleteListener(this)
            }
        cont.invokeOnCancellation {
          listener.cancel()
        }
      }
    }
  }

  @Throws(ApiException::class)
  override suspend fun sendMessage(
    nodeId: String,
    path: String,
    data: ByteArray?
  ): Int {
    return withTimeout(sendMessageTimeoutMillis) {
      suspendCancellableCoroutine<Int> { cont ->
        val listener = CompleteEventHandler(cont)
            .apply {
              messageClient.sendMessage(nodeId, path, data)
                  .addOnCompleteListener(this)
            }
        cont.invokeOnCancellation {
          listener.cancel()
        }
      }
    }
  }

  @Throws(ApiException::class)
  override suspend fun addListener(listener: OnMessageReceivedListener): Void {
    return withTimeout(addListenerTimeoutMills) {
      suspendCancellableCoroutine<Void> { cont ->
        val l = CompleteEventHandler(cont)
            .apply {
              messageClient.addListener(listener)
                  .addOnCompleteListener(this)
            }
        cont.invokeOnCancellation {
          l.cancel()
        }
      }
    }
  }

  @Throws(ApiException::class)
  override suspend fun removeListener(listener: OnMessageReceivedListener): Boolean {
    return suspendCoroutine { cont ->
      messageClient.removeListener(listener)
          .addOnCompleteListener(CompleteEventHandler(cont))
    }
  }
}

