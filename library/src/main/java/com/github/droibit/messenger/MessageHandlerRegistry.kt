package com.github.droibit.messenger

import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageEvent
import java.util.*


class MessageHandlerRegistry(
        private val messenger: Messenger,
        private val handlers: Map<String, MessageHandler>) : MessageApi.MessageListener {

    constructor(messenger: Messenger, handler: Pair<String, MessageHandler>)
        : this(messenger, Collections.singletonMap(handler.first, handler.second))
    
    override fun onMessageReceived(messageEvent: MessageEvent) {
        val handler = handlers.getValue(messageEvent.path)
        handler.onMessageReceived(messenger,
                        messageEvent.sourceNodeId,
                        messageEvent.data.toString(charset = Charsets.UTF_8)
        )
    }
}