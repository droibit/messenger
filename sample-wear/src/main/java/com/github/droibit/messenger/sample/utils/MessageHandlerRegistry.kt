package com.github.droibit.messenger.sample.utils

import com.github.droibit.messenger.Messenger
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageEvent
import java.util.Collections

class MessageHandlerRegistry(
  private val messenger: Messenger,
  private val handlers: Map<String, MessageHandler>
) : MessageApi.MessageListener {

  constructor(
    messenger: Messenger,
    handler: Pair<String, MessageHandler>
  ) : this(messenger, Collections.singletonMap(handler.first, handler.second))

  override fun onMessageReceived(messageEvent: MessageEvent) {
    val handler = handlers.getValue(messageEvent.path)
    handler.onMessageReceived(messenger, messageEvent)
  }
}