package com.github.droibit.messenger

import com.github.droibit.messenger.Messenger.Companion.KEY_MESSAGE_REJECTED
import com.github.droibit.messenger.internal.SuspendMessageSender
import com.google.android.gms.wearable.MessageEvent
import com.nhaarman.mockito_kotlin.*
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class MessengerTest {

    @JvmField
    @Rule
    val rule = MockitoJUnit.rule()

    @Mock
    private lateinit var messageSender: SuspendMessageSender

    @Mock
    private lateinit var handlers: Map<String, MessageHandler>

    @Mock
    private lateinit var messageRejector: MessageRejector

    @Mock
    private lateinit var ignoreNodes: Set<String>

    @InjectMocks
    private lateinit var messenger: Messenger

    @Test
    fun rejectMessage() {
        whenever(messageRejector.invoke(anyString())).thenReturn(true)

        val handler = mock<MessageHandler>()
        whenever(handlers[KEY_MESSAGE_REJECTED]).thenReturn(handler)

        val event = mock<MessageEvent> {
            on { path } doReturn "/path"
            on { sourceNodeId } doReturn "nodeId"
        }
        messenger.onMessageReceived(event)

        verify(handler).onMessageReceived(same(messenger), anyString(), anyString())
        verify(handlers, never())[event.path]
    }

    @Test
    fun callbackMessage() {
        whenever(messageRejector.invoke(anyString())).thenReturn(false)

        val event = mock<MessageEvent> {
            on { path } doReturn "/path"
            on { sourceNodeId } doReturn "nodeId"
            on { data } doReturn "data".toByteArray(charset = Charsets.UTF_8)
        }
        val handler = mock<MessageHandler>()
        whenever(handlers[event.path]).thenReturn(handler)
        messenger.onMessageReceived(event)

        verify(handler).onMessageReceived(
                same(messenger),
                eq("nodeId"),
                eq("data")
        )
        verify(handlers, never())[KEY_MESSAGE_REJECTED]
    }

    @Test
    fun failedToGetConnectedNodes() {
        // TODO: Pending until mockito-kotlin supports suspend function.
    }

    @Test
    fun ignoringSpecifiedNodeFromConnectedNodes() {
        // TODO: Pending until mockito-kotlin supports suspend function.
    }

    @Test
    fun failedToSendMessage() {
        // TODO: Pending until mockito-kotlin supports suspend function.
    }

    @Test
    fun succeededSendingMessage() {
        // TODO: Pending until mockito-kotlin supports suspend function.
    }
}