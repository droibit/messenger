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
     * @param data data to be associated with the path
     */
    fun onMessageReceived(messenger: Messenger, data: String?)
}
