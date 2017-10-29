package com.github.droibit.messenger

/**
 * It is called to receive the message of the registered path.
 */
interface MessageHandler {

    /**
     * Called in the message receive.
     *
     * @param messenger messenger object
     * @param sourceNodeId node ID of the sender.
     * @param data payload to be associated with the path. If there is no payload, empty string.
     */
    fun onMessageReceived(messenger: Messenger, sourceNodeId: String, data: String)
}
