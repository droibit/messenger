package com.github.droibit.messenger.internal

import com.google.android.gms.wearable.MessageEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Rule
import org.junit.Test
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
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
      assertThat(actualMessageEvent).isSameAs(expMessageEvent)
    } catch (e: CancellationException) {
      fail(e.message)
    }
  }

  @Test
  fun obtain_timeout() = runTest {
    try {
      val messenger = MessageEventHandler(setOf(), 100L, dispatcher)
      messenger.obtain()
      fail<Unit>("error")
    } catch (e: Exception) {
      assertThat(e)
        .isExactlyInstanceOf(TimeoutCancellationException::class.java)
    }
    assertThat(dispatcher.continuation).isNull()
  }
}