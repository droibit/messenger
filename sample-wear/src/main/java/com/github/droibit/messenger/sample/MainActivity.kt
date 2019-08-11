package com.github.droibit.messenger.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.github.droibit.messenger.Messenger
import com.github.droibit.messenger.sample.model.ConfirmMessageHandler
import com.github.droibit.messenger.sample.model.ConfirmMessageHandler.Companion.PATH_ERROR_MESSAGE
import com.github.droibit.messenger.sample.model.ConfirmMessageHandler.Companion.PATH_SUCCESS_MESSAGE
import com.github.droibit.messenger.sample.model.ResponseMessageHandler
import com.github.droibit.messenger.sample.model.ResponseMessageHandler.Companion.PATH_REQUEST_MESSAGE
import com.github.droibit.messenger.sample.model.StandardMessageHandler
import com.github.droibit.messenger.sample.model.StandardMessageHandler.Companion.PATH_DEFAULT_MESSAGE
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {

  private val messageClient: MessageClient by lazy {
    Wearable.getMessageClient(this)
  }

  private val messenger: Messenger by lazy {
    Messenger.Builder(this)
        .getNodesTimeout(2_000L)
        .sendMessageTimeout(2_500L)
        .build()
  }

  private val handlers = hashMapOf(
      PATH_DEFAULT_MESSAGE to StandardMessageHandler(this),
      PATH_SUCCESS_MESSAGE to ConfirmMessageHandler(this, PATH_SUCCESS_MESSAGE),
      PATH_ERROR_MESSAGE to ConfirmMessageHandler(this, PATH_ERROR_MESSAGE),
      PATH_REQUEST_MESSAGE to ResponseMessageHandler()
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  override fun onResume() {
    super.onResume()
    messageClient.addListener(this)
  }

  override fun onPause() {
    messageClient.removeListener(this)
    super.onPause()
  }

  override fun onMessageReceived(messageEvent: MessageEvent) {
    val handler = handlers.getValue(messageEvent.path)
    handler.onMessageReceived(messenger, messageEvent)
  }
}
