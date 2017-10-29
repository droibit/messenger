package com.github.droibit.messenger

import android.support.annotation.VisibleForTesting
import com.github.droibit.messenger.internal.NoneTimeoutSuspendMessageSender
import com.github.droibit.messenger.internal.SuspendMessageSender
import com.github.droibit.messenger.internal.TimeoutSuspendMessageSender
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.MessageApi.MessageListener
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node

/**
 * Class for communication between the wear and handheld using the Message API.
 * When you register a receiver, it will be automatically called back when the message is received.
 *
 *
 * Whether whether to reject the message, use the [MessageRejector].
 * Receiver at the time rejected to register using the [KEY_MESSAGE_REJECTED].
 */
class Messenger @VisibleForTesting internal constructor(
        private val messageSender: SuspendMessageSender,
        private val handlers: Map<String, MessageHandler>,
        private val ignoreNodes: Set<String>) : MessageListener {

    /**
     * The utility class that simplifies the registration of receiver.
     */
    class Builder(private val googleApiClient: GoogleApiClient) {

        internal val handlers: MutableMap<String, MessageHandler> = hashMapOf()

        internal var ignoreNodes: Set<String> = hashSetOf()

        internal val suspendMessageSender: SuspendMessageSender
            get() = newSuspendMessenger(connectNodesMillis, sendMessageMillis)

        private var connectNodesMillis: Long? = null

        private var sendMessageMillis: Long? = null

        /**
         * Register a new handler.
         */
        fun register(handler: MessageHandler): Builder {
            return also { it.handlers.put(handler.path, handler) }
        }

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
         * Set display name([Node.getDisplayName]) of the connected node to be ignored.
         */
        fun ignoreNodes(vararg nodeDisplayNames: String): Builder {
            return also { it.ignoreNodes = nodeDisplayNames.toSet() }
        }

        /**
         * Get a new instance of the Messenger.
         */
        fun build() = Messenger(this)

        // Private

        private fun newSuspendMessenger(connectNodesMillis: Long?, sendMessageMillis: Long?): SuspendMessageSender {
            return if (connectNodesMillis != null && sendMessageMillis != null) {
                TimeoutSuspendMessageSender(googleApiClient, connectNodesMillis, sendMessageMillis)
            } else {
                NoneTimeoutSuspendMessageSender(googleApiClient)
            }
        }
    }

    private constructor(builder: Builder) : this(
            messageSender = builder.suspendMessageSender,
            handlers = builder.handlers,
            ignoreNodes = builder.ignoreNodes
    )

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val data = messageEvent.data?.toString(charset = Charsets.UTF_8) ?: ""
        handlers.getValue(messageEvent.path)
                .onMessageReceived(this, messageEvent.sourceNodeId, data)
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
                .filter { it.displayName !in ignoreNodes }
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
