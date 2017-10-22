package com.github.droibit.messenger

import com.github.droibit.messenger.Messenger.Companion.KEY_MESSAGE_REJECTED
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.wearable.MessageApi.MessageListener
import com.google.android.gms.wearable.MessageApi.SendMessageResult
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult
import com.google.android.gms.wearable.Wearable

typealias MessageRejector = ((String?) -> Boolean)

/**
 * Class for communication between the wear and handheld using the Message API.
 * When you register a receiver, it will be automatically called back when the message is received.
 *
 *
 * Whether whether to reject the message, use the [MessageRejector].
 * Receiver at the time rejected to register using the [KEY_MESSAGE_REJECTED].
 */
class Messenger(private val googleApiClient: GoogleApiClient) : MessageListener {

    /**
     * The utility class that simplifies the registration of receiver.
     */
    class Builder(googleApiClient: GoogleApiClient) {

        private val messenger = Messenger(googleApiClient)

        /**
         * Register a new handler.
         */
        fun register(handler: MessageHandler): Builder {
            messenger.handlers.put(handler.path, handler)
            return this
        }

        /**
         * Set the [MessageRejector]. [MessageRejector] will use in the case of a decision related to the whole.
         * (e.g. Network is not connected)
         */
        fun rejectDecider(rejectDecider: MessageRejector): Builder {
            messenger.rejectDecider = rejectDecider
            return this
        }

        /**
         * Get a new instance of the Messenger.
         */
        fun build(): Messenger {
            return messenger
        }
    }

    private val handlers = hashMapOf<String, MessageHandler>()

    private var rejectDecider: MessageRejector? = null

    override fun onMessageReceived(messageEvent: MessageEvent) {
        val data = messageEvent.data?.toString(charset = Charsets.UTF_8)
        if (rejectDecider?.invoke(data) == true) {
            handlers.getValue(KEY_MESSAGE_REJECTED).onMessageReceived(this, data)
            return
        }

        val receiver = handlers[messageEvent.path]
                ?: error("Callback corresponding to the path(${messageEvent.path}) is not registered.")
        receiver.onMessageReceived(this, data)
    }

    /**
     * Sends payload to path.
     *
     * @param path     specified path
     * @param data     data to be associated with the path
     * @param callback callback of send message
     */
    fun sendMessage(path: String, data: String?, callback: MessageCallback? = null) {
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
