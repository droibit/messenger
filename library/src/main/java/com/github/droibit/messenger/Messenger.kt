package com.github.droibit.messenger

import android.content.Context
import androidx.annotation.Size
import androidx.annotation.VisibleForTesting
import com.github.droibit.messenger.internal.MessageEventHandler
import com.github.droibit.messenger.internal.WearableClient
import com.github.droibit.messenger.internal.WearableClient.ClientProvider
import com.github.droibit.messenger.internal.WearableClientImpl
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.WearableStatusCodes
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

typealias ExcludeNode = (Node) -> Boolean

class Messenger @VisibleForTesting internal constructor(
  private val client: WearableClient,
  private val messageHandlerFactory: MessageEventHandler.Factory,
  private val excludeNode: ExcludeNode
) {

  constructor(builder: Builder) : this(
    WearableClientImpl(
      ClientProvider(builder.context),
      builder.getNodesMillis,
      builder.sendMessageMillis
    ),
    builder.listenerFactory,
    builder.excludeNode
  )

  /**
   * Notifies the message events sent to the calling device.
   */
  @ExperimentalCoroutinesApi
  val messageEvents: Flow<MessageEvent> get() = client.messageEvents

  /**
   * Send payload to path.
   *
   * @param path     specified path
   * @param data     data to be associated with the path
   * @param strictSend If true, [ApiException] is thrown if node (filtered) is not connected.
   * @throws ApiException
   * @throws CancellationException
   */
  @Throws(ApiException::class, CancellationException::class)
  suspend fun sendMessage(
    path: String,
    data: ByteArray? = null,
    strictSend: Boolean = false
  ) {
    val filteredNodes = client.getConnectedNodes()
      .filter { !excludeNode.invoke(it) }
    if (strictSend && filteredNodes.isEmpty()) {
      throw ApiException(Status(WearableStatusCodes.TARGET_NODE_NOT_CONNECTED))
    }
    filteredNodes.forEach { client.sendMessage(it.id, path, data) }
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
    data: ByteArray? = null
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
    sendData: ByteArray? = null,
    @Size(min = 1L) expectedPaths: Set<String>
  ): MessageEvent {
    val nodes = client.getConnectedNodes()
      .filter { !excludeNode.invoke(it) }
    if (nodes.isEmpty()) {
      throw ApiException(Status(WearableStatusCodes.TARGET_NODE_NOT_CONNECTED))
    }
    return obtainMessage(nodes.first().id, sendPath, sendData, expectedPaths)
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
    sendData: ByteArray? = null,
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
  ) = client.getCapability(capability, nodeFilter)

  /**
   * Gets a list of nodes to which this device is currently connected,
   * either directly or indirectly via a directly connected node.
   */
  @Throws(ApiException::class, CancellationException::class)
  suspend fun getConnectedNodes(useExcludeNode: Boolean = true): List<Node> {
    val connectedNodes = client.getConnectedNodes()
    if (useExcludeNode) {
      return connectedNodes.filterNot(excludeNode)
    }
    return connectedNodes
  }

  class Builder(internal val context: Context) {

    internal var excludeNode: (Node) -> Boolean = { false }

    internal var getNodesMillis = 2_500L

    internal var sendMessageMillis = 5_000L

    private var waitMessageMillis = 10_000L

    internal val listenerFactory: MessageEventHandler.Factory
      get() = MessageEventHandler.Factory(waitMessageMillis)

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
    fun build() = Messenger(this)
  }
}