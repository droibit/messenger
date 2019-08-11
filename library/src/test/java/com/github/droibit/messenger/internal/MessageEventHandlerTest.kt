package com.github.droibit.messenger.internal

import com.google.android.gms.wearable.MessageEvent
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.fail
import org.assertj.core.api.Java6Assertions
import org.junit.Rule
import org.junit.Test
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class MessageEventHandlerTest {

  @get:Rule
  val rule: MockitoRule = MockitoJUnit.rule()

  @Spy
  private lateinit var dispatcher: MessageEventHandler.Dispatcher

  @Test
  fun onMessageReceived_messageEventIncludeExpectedPath() {
    doNothing().whenever(dispatcher)
        .dispatchMessageEvent(any())

    val handler = MessageEventHandler(setOf("test1", "test2"), Long.MAX_VALUE, dispatcher)
    val expMessageEvent = mock<MessageEvent> {
      on { path } doReturn "test1"
    }
    handler.onMessageReceived(expMessageEvent)

    verify(dispatcher).dispatchMessageEvent(expMessageEvent)
  }

  @Test
  fun onMessageReceived_messageEventNotIncludeExpectedPath() {
    doNothing().whenever(dispatcher)
        .dispatchMessageEvent(any())

    val handler = MessageEventHandler(setOf("test1", "test2"), Long.MAX_VALUE, dispatcher)
    val expMessageEvent = mock<MessageEvent> {
      on { path } doReturn "test3"
    }
    handler.onMessageReceived(expMessageEvent)

    verify(dispatcher, never()).dispatchMessageEvent(any())
  }

  @Test
  fun obtain() = runBlocking<Unit> {
    try {
      val expMessageEvent = mock<MessageEvent>()
      val messenger = MessageEventHandler(setOf(), 150L, dispatcher)
      launch {
        delay(100L)
        dispatcher.dispatchMessageEvent(expMessageEvent)
      }
      val actualMessageEvent = messenger.obtain()
      Java6Assertions.assertThat(actualMessageEvent)
          .isSameAs(expMessageEvent)
    } catch (e: CancellationException) {
      Assertions.fail(e.message)
    }
  }

  @Test
  fun obtain_timeout() = runBlocking<Unit> {
    try {
      val messenger = MessageEventHandler(setOf(), 100L, dispatcher)
      messenger.obtain()
      fail<Unit>("error")
    } catch (e: Exception) {
      Java6Assertions.assertThat(e)
          .isExactlyInstanceOf(TimeoutCancellationException::class.java)
    }
    Java6Assertions.assertThat(dispatcher.continuation)
        .isNull()
  }
}