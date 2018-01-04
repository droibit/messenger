package com.github.droibit.messenger

import android.content.Context
import android.os.Bundle
import android.support.annotation.Size
import android.support.annotation.VisibleForTesting
import android.support.annotation.WorkerThread
import com.github.droibit.messenger.internal.GoogleApiConnectionHandler
import com.github.droibit.messenger.internal.MessageEventHandler
import com.github.droibit.messenger.internal.SuspendMessageSender
import com.github.droibit.messenger.internal.SuspendMessageSenderImpl
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import kotlinx.coroutines.experimental.withTimeout
import java.util.concurrent.TimeUnit

typealias ExcludeNode = (Node) -> Boolean

/**
 * Class for communication between the wear and handheld using the Message API.
 * When you register a receiver, it will be automatically called back when the message is received.
 */
class Messenger @VisibleForTesting internal constructor(
        private val apiClient: GoogleApiClient,
        private val messageSender: SuspendMessageSender,
        private val eventHandlerFactory: MessageEventHandler.Factory,
        private val excludeNode: ExcludeNode) {

    /**
     * The utility class that simplifies the registration of receiver.
      */
    class Builder(internal val googleApiClient: GoogleApiClient) {

        internal var excludeNode: (Node) -> Boolean = { false }

        internal val suspendMessageSender: SuspendMessageSender
            get() = SuspendMessageSenderImpl(
                    googleApiClient,
                    Wearable.NodeApi,
                    Wearable.MessageApi,
                    Wearable.CapabilityApi,
                    getNodesMillis, sendMessageMillis
            )

        internal val listenerFactory: MessageEventHandler.Factory
            get() = MessageEventHandler.Factory(waitMessageMillis)

        private var getNodesMillis = 2_500L

        private var sendMessageMillis = 5_000L

        private var waitMessageMillis = 10_000L

        constructor(context: Context) : this(
                GoogleApiClient.Builder(context)
                        .addApi(Wearable.API)
                        .build()
        )

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
        fun obtainMessageTimeout(sendMessageMillis: Long, waitMessageMillis: Long): Builder {
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

    private constructor(builder: Builder) : this(
            apiClient = builder.googleApiClient,
            messageSender = builder.suspendMessageSender,
            eventHandlerFactory = builder.listenerFactory,
            excludeNode = builder.excludeNode
    )

    val isConnected: Boolean get() = apiClient.isConnected

    val isConnecting: Boolean get() = apiClient.isConnecting

    @WorkerThread
    fun blockingConnect(timeoutMillis: Long): ConnectionResult {
        return apiClient.blockingConnect(timeoutMillis, TimeUnit.MILLISECONDS)
    }

    suspend fun connect(timeoutMillis: Long): ConnectionResult {
        if (apiClient.isConnected) {
            return ConnectionResult(CommonStatusCodes.SUCCESS)
        }

        return withTimeout(timeoutMillis) {
            suspendCancellableCoroutine<ConnectionResult> { cont ->
                val handler = object : GoogleApiConnectionHandler() {
                    override fun onConnected(connectionHint: Bundle?)
                            = cont.resume(ConnectionResult(CommonStatusCodes.SUCCESS))

                    override fun onConnectionFailed(result: ConnectionResult)
                            = cont.resume(result)
                }.apply {
                    apiClient.registerConnectionCallbacks(this)
                    apiClient.registerConnectionFailedListener(this)
                }
                apiClient.connect()

                cont.invokeOnCompletion {
                    apiClient.unregisterConnectionCallbacks(handler)
                    apiClient.unregisterConnectionFailedListener(handler)
                    if (cont.isCancelled) apiClient.disconnect()
                }
            }
        }
    }

    fun disconnect() {
        apiClient.disconnect()
    }

    /**
     * Send payload to path.
     *
     * @param path     specified path
     * @param data     data to be associated with the path
     * @return result of send message.
     */
    suspend fun sendMessage(path: String, data: ByteArray?): Status {
        val connectedNodesResult = messageSender.getConnectedNodes()
        if (!connectedNodesResult.status.isSuccess) {
            return connectedNodesResult.status
        }

        connectedNodesResult.nodes
                .filter { !excludeNode.invoke(it) }
                .forEach {
                    val sendMessageResult = messageSender.sendMessage(it.id, path, data)
                    if (!sendMessageResult.status.isSuccess) {
                        return sendMessageResult.status
                    }
                }
        return Status(CommonStatusCodes.SUCCESS)
    }

    /**
     * Send payload to path.
     *
     * @param nodeId   the nodeID
     * @param path     specified path
     * @param data     data to be associated with the path
     * @return result of send message.
     */
    suspend fun sendMessage(nodeId: String, path: String, data: ByteArray?): Status =
            messageSender.sendMessage(nodeId, path, data).status

    /**
     * Send payload to sendPath and obtain response message.
     *
     * @param nodeId   the nodeID
     * @param sendPath     specified sendPath
     * @param sendData     sendData to be associated with the sendPath
     * @param expectedPaths Response message sendPath set
     * @return response message
     * @throws MessengerException
     * @throws TimeoutCancellationException
     */
    @Throws(MessengerException::class, TimeoutCancellationException::class)
    suspend fun obtainMessage(nodeId: String, sendPath: String, sendData: ByteArray?,
            @Size(min = 1L) expectedPaths: Set<String>): MessageEvent {
        val handler = eventHandlerFactory.create(expectedPaths)
        var addListenerStatus: Status? = null
        try {
            addListenerStatus = messageSender.addListener(handler)
            if (!addListenerStatus.isSuccess) {
                throw MessengerException(error = addListenerStatus)
            }

            val sendMessageResultStatus = sendMessage(nodeId, sendPath, sendData)
            if (!sendMessageResultStatus.isSuccess) {
                throw MessengerException(error = sendMessageResultStatus)
            }
            return handler.obtain()
        } finally {
            if (addListenerStatus?.isSuccess == true) {
                messageSender.removeListener(handler)
            }
        }
    }

    /**
     * Send payload to sendPath and obtain response message.
     *
     * @param sendPath     specified sendPath
     * @param sendData     sendData to be associated with the sendPath
     * @param expectedPaths Response message sendPath set
     * @return response message
     * @throws MessengerException
     * @throws TimeoutCancellationException
     */
    @Throws(MessengerException::class, TimeoutCancellationException::class)
    suspend fun obtainMessage(sendPath: String, sendData: ByteArray?,
            @Size(min = 1L) expectedPaths: Set<String>): MessageEvent {
        val handler = eventHandlerFactory.create(expectedPaths)
        var addListenerStatus: Status? = null
        try {
            addListenerStatus = messageSender.addListener(handler)
            if (!addListenerStatus.isSuccess) {
                throw MessengerException(error = addListenerStatus)
            }

            val sendMessageResultStatus = sendMessage(sendPath, sendData)
            if (!sendMessageResultStatus.isSuccess) {
                throw MessengerException(error = sendMessageResultStatus)
            }
            return handler.obtain()
        } finally {
            if (addListenerStatus?.isSuccess == true) {
                messageSender.removeListener(handler)
            }
        }
    }

    /**
     * Get nodes with specified capability.
     */
    @Throws(MessengerException::class)
    suspend fun getCapability(capability: String, nodeFilter: Int): CapabilityInfo {
        val getCapabilityResult = messageSender.getCapability(capability, nodeFilter)
        if (!getCapabilityResult.status.isSuccess) {
            throw MessengerException(error = getCapabilityResult.status)
        }
        return getCapabilityResult.capability
    }
}
