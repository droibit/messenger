package com.github.droibit.messenger

import android.content.Context
import android.support.annotation.VisibleForTesting
import android.support.annotation.WorkerThread
import com.github.droibit.messenger.internal.SuspendMessageSender
import com.github.droibit.messenger.internal.SuspendMessageSenderImpl
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import java.util.concurrent.TimeUnit

typealias ExcludeNode = (Node) -> Boolean

/**
 * Class for communication between the wear and handheld using the Message API.
 * When you register a receiver, it will be automatically called back when the message is received.
 */
class Messenger @VisibleForTesting internal constructor(
        private val googleApiClient: GoogleApiClient,
        private val messageSender: SuspendMessageSender,
        private val excludeNode: ExcludeNode) {

    /**
     * The utility class that simplifies the registration of receiver.
     */
    class Builder(internal val googleApiClient: GoogleApiClient) {

        internal var excludeNode: (Node) -> Boolean = { false }

        internal val suspendMessageSender: SuspendMessageSender
            get() = SuspendMessageSenderImpl(googleApiClient, connectNodesMillis, sendMessageMillis)

        private var connectNodesMillis = 5000L

        private var sendMessageMillis = 2500L

        constructor(context: Context) : this(
                GoogleApiClient.Builder(context)
                        .addApi(Wearable.API)
                        .build()
        )

        /**
         * Set timeout(ms).
         */
        fun timeout(connectNodesMillis: Long, sendMessageMillis: Long): Builder {
            require(connectNodesMillis > 0)
            require(sendMessageMillis > 0)
            return also {
                it.connectNodesMillis = connectNodesMillis
                it.sendMessageMillis = sendMessageMillis
            }
        }

        /**
         * Set predicate to ignore the connected node.
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
            googleApiClient = builder.googleApiClient,
            messageSender = builder.suspendMessageSender,
            excludeNode = builder.excludeNode
    )

    val isConnected: Boolean get() = googleApiClient.isConnected

    @WorkerThread
    fun blockingConnect(timeoutMillis: Int): ConnectionResult {
        return googleApiClient.blockingConnect(timeoutMillis.toLong(), TimeUnit.MILLISECONDS)
    }

    fun disconnect() {
        googleApiClient.disconnect()
    }

    /**
     * Sends payload to path.
     *
     * @param path     specified path
     * @param data     data to be associated with the path
     * @return result of send message.
     */
    suspend fun sendMessage(path: String, data: String?): Status {
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
     * Sends payload to path.
     *
     * @param nodeId   the nodeID
     * @param path     specified path
     * @param data     data to be associated with the path
     * @return result of send message.
     */
    suspend fun sendMessage(nodeId: String, path: String, data: String?): Status {
        val sendMessageResult = messageSender.sendMessage(nodeId, path, data)
        return sendMessageResult.status
    }
}
