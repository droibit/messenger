package com.github.droibit.messenger.internal

import com.github.droibit.messenger.internal.WearableClient.ClientProvider
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient.OnMessageReceivedListener
import com.google.android.gms.wearable.Node
import kotlinx.coroutines.experimental.CancellableContinuation
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import kotlinx.coroutines.experimental.withTimeout
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.experimental.suspendCoroutine

internal class WearableClientImpl(
  private val clientClientProvider: ClientProvider,
  private val getNodesTimeoutMillis: Long,
  private val sendMessageTimeoutMillis: Long,
  private val addListenerTimeoutMills: Long
) : WearableClient {

  private val nodeClient get() = clientClientProvider.nodeClient

  private val messageClient get() = clientClientProvider.messageClient

  private val capabilityClient get() = clientClientProvider.capabilityClient

  @Throws(ApiException::class)
  override suspend fun getConnectedNodes(): List<Node> {
    return withTimeout(getNodesTimeoutMillis) {
      suspendCancellableCoroutine<List<Node>> { cont ->
        val listener = OnCompleteListenerImpl(cont)
            .apply {
              nodeClient.connectedNodes.addOnCompleteListener(this)
            }
        cont.invokeOnCompletion(onCancelling = true) {
          listener.cancel()
        }
      }
    }
  }

  override suspend fun getCapability(
    capability: String,
    nodeFilter: Int
  ): CapabilityInfo {
    return withTimeout(getNodesTimeoutMillis) {
      suspendCancellableCoroutine<CapabilityInfo> { cont ->
        val listener = OnCompleteListenerImpl(cont)
            .apply {
              capabilityClient.getCapability(capability, nodeFilter)
                  .addOnCompleteListener(this)
            }
        cont.invokeOnCompletion(onCancelling = true) {
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
        val listener = OnCompleteListenerImpl(cont)
            .apply {
              messageClient.sendMessage(nodeId, path, data)
                  .addOnCompleteListener(this)
            }
        cont.invokeOnCompletion(onCancelling = true) {
          listener.cancel()
        }
      }
    }
  }

  @Throws(ApiException::class)
  override suspend fun addListener(listener: OnMessageReceivedListener): Void {
    return withTimeout(addListenerTimeoutMills) {
      suspendCancellableCoroutine<Void> { cont ->
        val l = OnCompleteListenerImpl(cont)
            .apply {
              messageClient.addListener(listener)
                  .addOnCompleteListener(this)
            }
        cont.invokeOnCompletion(onCancelling = true) {
          l.cancel()
        }
      }
    }
  }

  @Throws(ApiException::class)
  override suspend fun removeListener(listener: OnMessageReceivedListener): Boolean {
    return suspendCancellableCoroutine { cont ->
      messageClient.removeListener(listener)
          .addOnCompleteListener(OnCompleteListenerImpl(cont))
    }
  }
}

private class OnCompleteListenerImpl<TResult>(cont: CancellableContinuation<TResult>) :
    OnCompleteListener<TResult> {

  private var raw = AtomicReference<(Task<TResult>) -> Unit>({
    if (it.isSuccessful) {
      cont.resume(it.result)
      return@AtomicReference
    }

    val e = it.exception as? ApiException
    if (e == null) {
      cont.resumeWithException(ApiException(Status(CommonStatusCodes.ERROR)))
    } else {
      cont.resumeWithException(e)
    }
  })

  override fun onComplete(task: Task<TResult>) {
    val raw = this.raw.get()
    raw?.invoke(task)
  }

  fun cancel() = raw.set(null)
}