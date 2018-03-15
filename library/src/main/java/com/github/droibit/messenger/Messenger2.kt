package com.github.droibit.messenger

import android.content.Context
import android.support.annotation.Size
import com.github.droibit.messenger.Messenger2.Builder.Companion.ADD_LISTENER_TIMEOUT_MILLIS
import com.github.droibit.messenger.internal.MessageEventHandler2
import com.github.droibit.messenger.internal.WearableClient
import com.github.droibit.messenger.internal.WearableClient.ClientProvider
import com.github.droibit.messenger.internal.WearableClientImpl
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.WearableStatusCodes
import kotlinx.coroutines.experimental.CancellationException

class Messenger2 internal constructor(
  private val client: WearableClient,
  private val messageHandlerFactory: MessageEventHandler2.Factory,
  private val excludeNode: ExcludeNode
) {

  constructor(builder: Builder) : this(
      WearableClientImpl(
          ClientProvider(builder.context),
          builder.getNodesMillis,
          builder.sendMessageMillis,
          ADD_LISTENER_TIMEOUT_MILLIS
      ),
      builder.listenerFactory,
      builder.excludeNode
  )

  /**
   * Send payload to path.
   *
   * @param path     specified path
   * @param data     data to be associated with the path
   * @throws ApiException
   * @throws CancellationException
   */
  @Throws(ApiException::class, CancellationException::class)
  suspend fun sendMessage(
    path: String,
    data: ByteArray?
  ) {
    client.getConnectedNodes()
        .filter { !excludeNode.invoke(it) }
        .forEach {
          client.sendMessage(it.id, path, data)
        }
  }

  /**
   * Send payload to path.
   *
   * @param nodeId   the nodeID
   * @param path     specified path
   * @param data     data to be associated with the path
   * @throws ApiException
   * @throws CancellationException
   */
  @Throws(ApiException::class, CancellationException::class)
  suspend fun sendMessage(
    nodeId: String,
    path: String,
    data: ByteArray?
  ) = client.sendMessage(nodeId, path, data)

  /**
   * Send payload to sendPath and obtain response message.
   *
   * @param sendPath     specified sendPath
   * @param sendData     sendData to be associated with the sendPath
   * @param expectedPaths Response message sendPath set
   * @return response message
   * @throws ApiException
   * @throws CancellationException
   */
  @Throws(ApiException::class, CancellationException::class)
  suspend fun obtainMessage(
    sendPath: String,
    sendData: ByteArray?,
    @Size(min = 1L) expectedPaths: Set<String>
  ): MessageEvent {
    val connectedNodes = client.getConnectedNodes()
    if (connectedNodes.isEmpty()) {
      throw ApiException(Status(WearableStatusCodes.TARGET_NODE_NOT_CONNECTED))
    }
    return obtainMessage(connectedNodes.first().id, sendPath, sendData, expectedPaths)
  }

  /**
   * Send payload to sendPath and obtain response message.
   *
   * @param nodeId   the nodeID
   * @param sendPath     specified sendPath
   * @param sendData     sendData to be associated with the sendPath
   * @param expectedPaths Response message sendPath set
   * @return response message
   * @throws ApiException
   * @throws CancellationException
   */
  @Throws(ApiException::class, CancellationException::class)
  suspend fun obtainMessage(
    nodeId: String,
    sendPath: String,
    sendData: ByteArray?,
    @Size(min = 1L) expectedPaths: Set<String>
  ): MessageEvent {
    val handler = messageHandlerFactory.create(expectedPaths)
    var listenerAdded = false
    try {
      client.addListener(handler)
      listenerAdded = true

      sendMessage(nodeId, sendPath, sendData)
      return handler.obtain()
    } finally {
      if (listenerAdded) client.removeListener(handler)
    }
  }

  /**
   * Get nodes with specified capability.
   */
  @Throws(ApiException::class, CancellationException::class)
  suspend fun getCapability(
    capability: String,
    nodeFilter: Int
  ): CapabilityInfo {
    return client.getCapability(capability, nodeFilter)
  }

  class Builder(internal val context: Context) {

    internal var excludeNode: (Node) -> Boolean = { false }

    internal var getNodesMillis = 2_500L

    internal var sendMessageMillis = 5_000L

    internal var waitMessageMillis = 10_000L

    internal val listenerFactory: MessageEventHandler2.Factory
      get() = MessageEventHandler2.Factory(waitMessageMillis)

    /**
     * Set timeout(ms) for getting message sending target node.
     */
    fun getNodesTimeout(getNodesMillis: Long): Builder {
      require(getNodesMillis > 0L)
      return also {
        it.getNodesMillis = getNodesMillis
      }
    }

    /**
     * Set timeout(ms) for message sending.
     */
    fun sendMessageTimeout(sendMessageMillis: Long): Builder {
      require(sendMessageMillis > 0L)
      return also {
        it.sendMessageMillis = sendMessageMillis
      }
    }

    /**
     * Set timeout for message(ms) obtaining.
     */
    fun obtainMessageTimeout(
      sendMessageMillis: Long,
      waitMessageMillis: Long
    ): Builder {
      require(sendMessageMillis > 0L)
      require(waitMessageMillis > 0L)
      return also {
        it.sendMessageMillis = sendMessageMillis
        it.waitMessageMillis = waitMessageMillis
      }
    }

    /**
     * Set predicate to ignore message sending target node.
     */
    fun excludeNode(predicate: ExcludeNode): Builder {
      return also { it.excludeNode = predicate }
    }

    /**
     * Get a new instance of the Messenger.
     */
    fun build() = Messenger2(this)

    companion object {
      internal const val ADD_LISTENER_TIMEOUT_MILLIS = 1_000L
    }
  }
}