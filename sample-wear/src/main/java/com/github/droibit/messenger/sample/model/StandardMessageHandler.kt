package com.github.droibit.messenger.sample.model

import android.app.Activity
import android.widget.Toast
import com.github.droibit.messenger.Messenger
import com.google.android.gms.wearable.MessageEvent
import timber.log.Timber

class StandardMessageHandler(private val activity: Activity) :
    MessageHandler {

  override fun onMessageReceived(
    messenger: Messenger,
    event: MessageEvent
  ) {
    val data = event.data.toString(Charsets.UTF_8)
    Timber.d("#onMessageReceived(path=$PATH_DEFAULT_MESSAGE, data=$data")
    activity.runOnUiThread {
      Toast.makeText(activity, data, Toast.LENGTH_SHORT).show()
    }
  }

  companion object {

    const val PATH_DEFAULT_MESSAGE = "/message"
  }
}
