package com.github.droibit.messenger.sample.utils

import com.github.droibit.messenger.Messenger
import com.google.android.gms.wearable.MessageEvent

/**
 * It is called to receive the message of the registered path.
 */
interface MessageHandler {

  /**
   * Called in the message receive.
   *
   * @param messenger messenger object
   * @param event message event
   */
  fun onMessageReceived(
    messenger: Messenger,
    event: MessageEvent
  )
}
