@file:Suppress("FunctionName")

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
import org.assertj.core.api.Assertions.fail
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit

class MessageHandlerTest {

    @Rule
    @JvmField
    val rule = MockitoJUnit.rule()

    @Spy
    private lateinit var dispatcher: MessageHandler.Dispatcher

    @Test
    fun onMessageReceived_messageEventIncludeExpectedPath() {
        doNothing().whenever(dispatcher).dispatchMessageEvent(any())

        val handler = MessageHandler(setOf("test1", "test2"), Long.MAX_VALUE, dispatcher)
        val expMessageEvent = mock<MessageEvent> {
            on { path } doReturn "test1"
        }
        handler.onMessageReceived(expMessageEvent)

        verify(dispatcher).dispatchMessageEvent(expMessageEvent)
    }

    @Test
    fun onMessageReceived_messageEventNotIncludeExpectedPath() {
        doNothing().whenever(dispatcher).dispatchMessageEvent(any())

        val handler = MessageHandler(setOf("test1", "test2"), Long.MAX_VALUE, dispatcher)
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
            val messenger = MessageHandler(setOf(), 150L, dispatcher)
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
    fun obtain_timeout() = runBlocking<Unit> {
        try {
            val messenger = MessageHandler(setOf(), 100L, dispatcher)
            messenger.obtain()
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isExactlyInstanceOf(TimeoutCancellationException::class.java)
        }
        assertThat(dispatcher.callback).isNull()
    }
}