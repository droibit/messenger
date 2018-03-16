package com.github.droibit.messenger.internal

import com.google.android.gms.wearable.MessageEvent
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doNothing
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.TimeoutCancellationException
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Java6Assertions
import org.junit.Rule
import org.junit.Test
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit

class MessageEventHandlerTest {

  @get:Rule
  val rule = MockitoJUnit.rule()

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
      Assertions.fail("error")
    } catch (e: Exception) {
      Java6Assertions.assertThat(e)
          .isExactlyInstanceOf(TimeoutCancellationException::class.java)
    }
    Java6Assertions.assertThat(dispatcher.continuation)
        .isNull()
  }
}