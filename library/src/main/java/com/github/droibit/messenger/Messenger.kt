package com.github.droibit.messenger

import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.annotation.WorkerThread
import com.github.droibit.messenger.internal.MessageHandler
import com.github.droibit.messenger.internal.SuspendDateItemPutter
import com.github.droibit.messenger.internal.SuspendDateItemPutterImpl
import com.github.droibit.messenger.internal.SuspendMessageSender
import com.github.droibit.messenger.internal.SuspendMessageSenderImpl
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.experimental.TimeoutCancellationException
import java.util.concurrent.TimeUnit

typealias ExcludeNode = (Node) -> Boolean

/**
 * Class for communication between the wear and handheld using the Message API.
 * When you register a receiver, it will be automatically called back when the message is received.
 */
class Messenger @VisibleForTesting internal constructor(
        private val googleApiClient: GoogleApiClient,
        private val messageSender: SuspendMessageSender,
        private val dataItemPutter: SuspendDateItemPutter,
        private val handlerFactory: MessageHandler.Factory,
        private val excludeNode: ExcludeNode) {

    /**
     * The utility class that simplifies the registration of receiver.
     */
    class Builder(internal val googleApiClient: GoogleApiClient) {

        internal var excludeNode: (Node) -> Boolean = { false }

        internal val suspendMessageSender: SuspendMessageSender
            get() = SuspendMessageSenderImpl(googleApiClient, connectNodesMillis, sendMessageMillis)

        internal val dataItemPutter: SuspendDateItemPutter
            get() = SuspendDateItemPutterImpl(googleApiClient, putDataItemTimeoutMillis)

        internal val listenerFactory: MessageHandler.Factory
            get() = MessageHandler.Factory(waitMessageMillis)

        private var connectNodesMillis = 5_000L

        private var sendMessageMillis = 5_000L

        private var waitMessageMillis = 10_000L

        private var putDataItemTimeoutMillis = 5_000L

        constructor(context: Context) : this(
                GoogleApiClient.Builder(context)
                        .addApi(Wearable.API)
                        .build()
        )

        /**
         * Set message sending timeout(ms).
         */
        fun sendMessageTimeout(connectNodesMillis: Long, sendMessageMillis: Long): Builder {
            require(connectNodesMillis > 0)
            require(sendMessageMillis > 0)
            return also {
                it.connectNodesMillis = connectNodesMillis
                it.sendMessageMillis = sendMessageMillis
            }
        }

        /**
         * Set message obtaining timeout(ms).
         */
        fun obtainMessageTimeout(connectNodesMillis: Long, sendMessageMillis: Long,
                waitMessageMillis: Long): Builder {
            require(connectNodesMillis > 0L)
            require(sendMessageMillis > 0L)
            require(waitMessageMillis > 0L)
            return also {
                it.connectNodesMillis = connectNodesMillis
                it.sendMessageMillis = sendMessageMillis
                it.waitMessageMillis = waitMessageMillis
            }
        }

        /**
         * Set data item creating timeout(ms).
         */
        fun putDataItemTimeout(timeoutMillis: Long): Builder {
            require(timeoutMillis > 0)
            return also { it.putDataItemTimeoutMillis = timeoutMillis }
        }

        /**
         * Set predicate to ignore the connected node.
         */
        fun excludeConnectedNode(predicate: ExcludeNode): Builder {
            return also { it.excludeNode = predicate }
        }

        /**
         * Get a new instance of the Messenger.
         */
        fun build() = Messenger(this)
    }

    private constructor(builder: Builder) : this(
            googleApiClient = builder.googleApiClient,
            messageSender = builder.suspendMessageSender,
            dataItemPutter = builder.dataItemPutter,
            handlerFactory = builder.listenerFactory,
            excludeNode = builder.excludeNode
    )

    val isConnected: Boolean get() = googleApiClient.isConnected

    val isConnecting: Boolean get() = googleApiClient.isConnecting

    @WorkerThread
    fun blockingConnect(timeoutMillis: Long): ConnectionResult {
        return googleApiClient.blockingConnect(timeoutMillis, TimeUnit.MILLISECONDS)
    }

    fun disconnect() {
        googleApiClient.disconnect()
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
     * @throws ObtainMessageException
     */
    @Throws(ObtainMessageException::class, TimeoutCancellationException::class)
    suspend fun obtainMessage(nodeId: String, sendPath: String, sendData: ByteArray?,
            expectedPaths: Set<String>): MessageEvent {
        val handler = handlerFactory.create(expectedPaths)
        try {
            val addListenerStatus = messageSender.addListener(handler)
            if (!addListenerStatus.isSuccess) {
                throw ObtainMessageException(error = addListenerStatus)
            }

            val sendMessageResultStatus = sendMessage(nodeId, sendPath, sendData)
            if (!sendMessageResultStatus.isSuccess) {
                throw ObtainMessageException(error = sendMessageResultStatus)
            }
            return handler.obtain()
        } finally {
            messageSender.removeListener(handler)
        }
    }

    /**
     * Send payload to sendPath and obtain response message.
     *
     * @param sendPath     specified sendPath
     * @param sendData     sendData to be associated with the sendPath
     * @param expectedPaths Response message sendPath set
     * @return response message
     */
    @Throws(ObtainMessageException::class, TimeoutCancellationException::class)
    suspend fun obtainMessage(sendPath: String, sendData: ByteArray?,
            expectedPaths: Set<String>): MessageEvent {
        val handler = handlerFactory.create(expectedPaths)
        try {
            val addListenerStatus = messageSender.addListener(handler)
            if (!addListenerStatus.isSuccess) {
                throw ObtainMessageException(error = addListenerStatus)
            }

            val sendMessageResultStatus = sendMessage(sendPath, sendData)
            if (!sendMessageResultStatus.isSuccess) {
                throw ObtainMessageException(error = sendMessageResultStatus)
            }
            return handler.obtain()
        } finally {
            messageSender.removeListener(handler)
        }
    }

    /**
     * Create new data item in Android Wear network.
     *
     * @param request Request to create a new data item.
     */
    suspend fun putDataItem(request: PutDataRequest): Status {
        val putDataItemResult = dataItemPutter.putDataItem(request)
        return putDataItemResult.status
    }
}
