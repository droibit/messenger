@file:Suppress("FunctionName")
package com.github.droibit.messenger.internal

import com.github.droibit.messenger.internal.MessageHandler.Dispatcher
import com.google.android.gms.wearable.MessageEvent
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import org.junit.Before
import org.junit.Test

class DispatcherTest {

    private lateinit var dispatcher: Dispatcher

    @Before
    fun setUp() {
        dispatcher = Dispatcher()
    }

    @Test
    fun dispatchMessageEvent() {
        val expCallback = mock<MessageCallback>()
        dispatcher.callback = expCallback

        val expMessageEvent = mock<MessageEvent>()
        dispatcher.dispatchMessageEvent(expMessageEvent)

        verify(expCallback).invoke(expMessageEvent)
    }

    @Test
    fun setCallback_alreadyHasMessageEvent() {
        val expMessageEvent = mock<MessageEvent>()
        dispatcher.dispatchMessageEvent(expMessageEvent)

        val expCallback = mock<MessageCallback>()
        dispatcher.callback = expCallback
        verify(expCallback).invoke(expMessageEvent)
    }

    @Test
    fun setCallback_hasNotMessageEvent() {
        val expCallback = mock<MessageCallback>()
        dispatcher.callback = expCallback

        verify(expCallback, never()).invoke(any())
    }
}