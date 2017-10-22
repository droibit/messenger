package com.github.droibit.messenger

import com.github.droibit.messenger.Messenger.Companion.KEY_MESSAGE_REJECTED
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.wearable.MessageApi.MessageListener
import com.google.android.gms.wearable.MessageApi.SendMessageResult
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult
import com.google.android.gms.wearable.Wearable
import java.util.concurrent.TimeUnit

typealias MessageRejector = ((String?) -> Boolean)

/**
 * Class for communication between the wear and handheld using the Message API.
 * When you register a receiver, it will be automatically called back when the message is received.
 *
 *
 * Whether whether to reject the message, use the [MessageRejector].
 * Receiver at the time rejected to register using the [KEY_MESSAGE_REJECTED].
 */
class Messenger private constructor(builder: Builder) : MessageListener {

    /**
     * The utility class that simplifies the registration of receiver.
     */
    class Builder(internal val googleApiClient: GoogleApiClient) {

        internal var messageRejector: MessageRejector = { false }

        internal val handlers: MutableMap<String, MessageHandler> = hashMapOf()

        internal var timeout: Timeout? = null

        /**
         * Register a new handler.
         */
        fun register(handler: MessageHandler): Builder {
            return also { it.handlers.put(handler.path, handler) }
        }

        /**
         * Set the [MessageRejector]. [MessageRejector] will use in the case of a decision related to the whole.
         * (e.g. Network is not connected)
         */
        fun rejectDecider(messageRejector: MessageRejector): Builder {
            return also { it.messageRejector = messageRejector }
        }

        /**
         * Set timeout(ms).
         */
        fun timeout(connectNodesMillis: Long, sendMessageMillis: Long): Builder {
            require(connectNodesMillis > 0)
            require(sendMessageMillis > 0)
            return also { it.timeout = Timeout(connectNodesMillis, sendMessageMillis) }
        }

        /**
         * Get a new instance of the Messenger.
         */
        fun build() = Messenger(this)
    }

    internal class Timeout(val connectNodesMillis: Long, val sendMessageMillis: Long)

    private val googleApiClient: GoogleApiClient
    private val handlers: MutableMap<String, MessageHandler>
    private val messageRejector: MessageRejector
    private var timeout: Timeout?

    init {
        googleApiClient = builder.googleApiClient
        handlers = builder.handlers
        messageRejector = builder.messageRejector
        timeout = builder.timeout
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val data = messageEvent.data?.toString(charset = Charsets.UTF_8) ?: ""
        if (messageRejector.invoke(data)) {
            handlers[KEY_MESSAGE_REJECTED]?.onMessageReceived(this, data)
            return
        }

        handlers.getValue(messageEvent.path)
                .onMessageReceived(this, data)
    }

    /**
     * Sends payload to path.
     *
     * @param path     specified path
     * @param data     data to be associated with the path
     * @param callback callback of send message
     */
    fun sendMessage(path: String, data: String?, callback: SendMessageCallback? = null) {
        getConnectedNodes().setResultCallback { nodesResult ->
            for (node in nodesResult.nodes) {
                val messageResult = sendMessage(node.id, path, data)
                if (callback == null) {
                    return@setResultCallback
                }
                messageResult.setResultCallback { sendMessageResult -> callback.onMessageResult(sendMessageResult.status) }
            }
        }
    }

    private fun sendMessageWithTimeout(
            path: String, data: String?, callback: SendMessageCallback?, timeout: Timeout) {
        getConnectedNodes().setResultCallback( { nodesResult ->
            if (!nodesResult.status.isSuccess) {
                callback?.onMessageResult(nodesResult.status)
                return@setResultCallback
            }

            nodesResult.nodes.forEach { node ->
                sendMessage(node.id, path, data).setResultCallback({ messageResult ->
                    if (!messageResult.status.isSuccess) {

                    }
                }, timeout.sendMessageMillis, TimeUnit.MILLISECONDS)

            }
        }, timeout.connectNodesMillis, TimeUnit.MILLISECONDS)
    }

    private fun sendMessage(nodeId: String, path: String, data: String?): PendingResult<SendMessageResult> {
        return Wearable.MessageApi.sendMessage(googleApiClient,
                nodeId,
                path,
                data?.toByteArray(charset = Charsets.UTF_8)
        )
    }

    private fun getConnectedNodes(): PendingResult<GetConnectedNodesResult> {
        return Wearable.NodeApi.getConnectedNodes(googleApiClient)
    }

    companion object {

        /**
         * The path of the reject receiver
         */
        @JvmField
        val KEY_MESSAGE_REJECTED = "/${BuildConfig.APPLICATION_ID}/rejected"
    }
}
