package com.github.droibit.messenger.sample.model

import com.github.droibit.messenger.Messenger
import com.google.android.gms.wearable.MessageEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class ResponseMessageHandler : MessageHandler {

  override fun onMessageReceived(
    messenger: Messenger,
    event: MessageEvent
  ) {
    GlobalScope.launch {
      val data = event.data.toString(Charsets.UTF_8)
      Timber.d("#onMessageReceived(path=$PATH_REQUEST_MESSAGE, data=$data")

      try {
        messenger.sendMessage(
            event.sourceNodeId, PATH_REQUEST_MESSAGE_FROM_WEAR,
            "Yeah!! from watch".toByteArray()
        )
        Timber.d("Succeed to send message in ${Thread.currentThread().name}.")
      } catch (e: Exception) {
        Timber.w(e)
      }
    }
  }

  companion object {

    const val PATH_REQUEST_MESSAGE = "/request_message"
    const val PATH_REQUEST_MESSAGE_FROM_WEAR = "/request_message_wear"
  }
}
