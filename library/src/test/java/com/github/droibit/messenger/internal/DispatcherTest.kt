@file:Suppress("FunctionName")

package com.github.droibit.messenger.internal

import com.github.droibit.messenger.internal.MessageEventHandler.Dispatcher
import com.google.android.gms.wearable.MessageEvent
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.experimental.Continuation

class DispatcherTest {

  private lateinit var dispatcher: Dispatcher

  @Before
  fun setUp() {
    dispatcher = Dispatcher()
  }

  @Test
  fun dispatchMessageEvent() {
    val expContinuation = mock<Continuation<MessageEvent>>()
    dispatcher.continuation = expContinuation

    val expMessageEvent = mock<MessageEvent>()
    dispatcher.dispatchMessageEvent(expMessageEvent)

    verify(expContinuation).resume(expMessageEvent)
  }

  @Test
  fun setCallback_alreadyHasMessageEvent() {
    val expMessageEvent = mock<MessageEvent>()
    dispatcher.dispatchMessageEvent(expMessageEvent)

    val expContinuation = mock<Continuation<MessageEvent>>()
    dispatcher.continuation = expContinuation
    verify(expContinuation).resume(expMessageEvent)
  }

  @Test
  fun setCallback_hasNotMessageEvent() {
    val expContinuation = mock<Continuation<MessageEvent>>()
    dispatcher.continuation = expContinuation

    verify(expContinuation, never()).resume(any())
  }
}