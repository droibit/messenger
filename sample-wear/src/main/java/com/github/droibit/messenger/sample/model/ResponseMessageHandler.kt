package com.github.droibit.messenger.sample.model

import com.github.droibit.messenger.Messenger
import com.github.droibit.messenger.sample.utils.MessageHandler
import com.google.android.gms.wearable.MessageEvent
import kotlinx.coroutines.experimental.launch
import timber.log.Timber

class ResponseMessageHandler : MessageHandler {

  override fun onMessageReceived(messenger: Messenger, event: MessageEvent) {
    val data = event.data.toString(Charsets.UTF_8)
    Timber.d("#onMessageReceived(path=$PATH_REQUEST_MESSAGE, data=$data")

    launch {
      val status = messenger.sendMessage(
          event.sourceNodeId, PATH_REQUEST_MESSAGE_FROM_WEAR,
          "Yeah!! from Android Wear".toByteArray()
      )
      if (status.isSuccess) {
        Timber.d("Succeed to send message in ${Thread.currentThread().name}.")
      } else {
        Timber.d(
            "Failed send message(code=${status.statusCode}, msg=${status.statusMessage}) in ${Thread.currentThread().name}"
        )
      }
    }
  }

  companion object {

    const val PATH_REQUEST_MESSAGE = "/request_message"
    const val PATH_REQUEST_MESSAGE_FROM_WEAR = "/request_message_wear"
  }
}
