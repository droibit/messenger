package com.github.droibit.messenger.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.github.droibit.messenger.Messenger
import com.github.droibit.messenger.sample.model.ConfirmMessageHandler
import com.github.droibit.messenger.sample.model.ConfirmMessageHandler.Companion.PATH_ERROR_MESSAGE
import com.github.droibit.messenger.sample.model.ConfirmMessageHandler.Companion.PATH_SUCCESS_MESSAGE
import com.github.droibit.messenger.sample.model.ResponseMessageHandler
import com.github.droibit.messenger.sample.model.ResponseMessageHandler.Companion.PATH_REQUEST_MESSAGE
import com.github.droibit.messenger.sample.model.StandardMessageHandler
import com.github.droibit.messenger.sample.model.StandardMessageHandler.Companion.PATH_DEFAULT_MESSAGE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class MainActivity : ComponentActivity(R.layout.activity_main) {

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

    lifecycleScope.launch {
      messenger.messageEvents
        .flowWithLifecycle(lifecycle)
        .collect {
          val handler = handlers.getValue(it.path)
          handler.onMessageReceived(messenger, it)
        }
    }
  }
}
