package com.github.droibit.messenger

/**
 * It is called to receive the message of the registered path.
 */
interface MessageHandler {

    /**
     * Get the path of this receiver to receive.
     */
    val path: String

    /**
     * Called in the message receive.
     *
     * @param messenger messenger object
     * @param data payload to be associated with the path. If there is no payload, empty string.
     */
    fun onMessageReceived(messenger: Messenger, data: String)
}
