package com.github.droibit.messenger

import com.github.droibit.messenger.internal.SuspendMessageSender
import com.google.android.gms.wearable.MessageEvent
import com.nhaarman.mockito_kotlin.*
import org.junit.Rule
import org.junit.Test
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
    private lateinit var ignoreNodes: Set<String>

    @InjectMocks
    private lateinit var messenger: Messenger

    @Test
    fun callbackMessage() {
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