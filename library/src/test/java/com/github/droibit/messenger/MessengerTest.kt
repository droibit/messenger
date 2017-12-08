@file:Suppress("FunctionName")

package com.github.droibit.messenger

import com.github.droibit.messenger.internal.MessageEventHandler
import com.github.droibit.messenger.internal.SuspendMessageSender
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.MessageApi.SendMessageResult
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.same
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Java6Assertions.assertThat
import org.assertj.core.api.Java6Assertions.fail
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import java.util.concurrent.TimeUnit

class MessengerTest {

    @JvmField
    @Rule
    val rule = MockitoJUnit.rule()!!

    @Mock
    private lateinit var apiClient: GoogleApiClient

    @Mock
    private lateinit var messageSender: SuspendMessageSender

    @Mock
    private lateinit var eventHandlerFactory: MessageEventHandler.Factory

    @Mock
    private lateinit var excludeNode: ExcludeNode

    @InjectMocks
    private lateinit var messenger: Messenger

    @Test
    fun isConnected() {
        whenever(apiClient.isConnected).thenReturn(true)

        assertThat(messenger.isConnected).isTrue()
        verify(apiClient).isConnected
        verify(apiClient, never()).isConnecting
    }

    @Test
    fun isConnecting() {
        whenever(apiClient.isConnecting).thenReturn(true)

        assertThat(messenger.isConnecting).isTrue()
        verify(apiClient).isConnecting
        verify(apiClient, never()).isConnected
    }

    @Test
    fun blockingConnect() {
        val expResult = ConnectionResult(CommonStatusCodes.SUCCESS)
        whenever(apiClient.blockingConnect(anyLong(), any())).thenReturn(expResult)

        val actualResult = messenger.blockingConnect(100L)
        assertThat(actualResult).isSameAs(expResult)

        verify(apiClient).blockingConnect(eq(100L), eq(TimeUnit.MILLISECONDS))
    }

    @Test
    fun disconnect() {
        messenger.disconnect()

        verify(apiClient).disconnect()
    }

    @Test
    fun sendMessage_success() = runBlocking<Unit> {
        whenever(excludeNode.invoke(any()))
                .thenReturn(true)
                .thenReturn(false)

        val node1 = mock<Node>()
        val node2 = mock<Node> { on { id } doReturn "node2" }
        val expGetConnectedNodeResult = mock<GetConnectedNodesResult> {
            on { nodes } doReturn listOf(node1, node2)
            on { status } doReturn Status(CommonStatusCodes.SUCCESS)
        }
        whenever(messageSender.getConnectedNodes()).thenReturn(expGetConnectedNodeResult)

        val expSendMessageResult = mock<SendMessageResult> {
            on { status } doReturn Status(CommonStatusCodes.SUCCESS)
        }
        whenever(messageSender.sendMessage(anyString(), anyString(), any()))
                .thenReturn(expSendMessageResult)

        val expData = byteArrayOf()
        val actualSendMessageResult = messenger.sendMessage("/path", expData)
        assertThat(actualSendMessageResult.status.isSuccess).isTrue()
        verify(messageSender).sendMessage(eq("node2"), eq("/path"), same(expData))
    }

    @Test
    fun sendMessage_failedToGetConnectedNodes() = runBlocking<Unit> {
        val expGetConnectedNodeResult = mock<GetConnectedNodesResult> {
            on { status } doReturn Status(CommonStatusCodes.ERROR)
        }
        whenever(messageSender.getConnectedNodes()).thenReturn(expGetConnectedNodeResult)

        val actualSendMessageResult = messenger.sendMessage("/path", null)
        assertThat(actualSendMessageResult.statusCode).isEqualTo(CommonStatusCodes.ERROR)
    }

    @Test
    fun sendMessage_failedToSendMessage() = runBlocking<Unit> {
        whenever(excludeNode.invoke(any())).thenReturn(false)

        val node = mock<Node> { on { id } doReturn "node" }
        val expGetConnectedNodeResult = mock<GetConnectedNodesResult> {
            on { nodes } doReturn listOf(node)
            on { status } doReturn Status(CommonStatusCodes.SUCCESS)
        }
        whenever(messageSender.getConnectedNodes()).thenReturn(expGetConnectedNodeResult)

        val expSendMessageResult = mock<SendMessageResult> {
            on { status } doReturn Status(CommonStatusCodes.ERROR)
        }
        whenever(messageSender.sendMessage(anyString(), anyString(), any()))
                .thenReturn(expSendMessageResult)

        val actualSendMessageResult = messenger.sendMessage("/path", byteArrayOf())
        assertThat(actualSendMessageResult.statusCode).isEqualTo(CommonStatusCodes.ERROR)
    }

    @Test
    fun sendMessage_specifyNodeId() = runBlocking<Unit> {
        val expSendMessageResult = mock<SendMessageResult> {
            on { status } doReturn Status(CommonStatusCodes.SUCCESS)
        }
        whenever(messageSender.sendMessage(anyString(), anyString(), any()))
                .thenReturn(expSendMessageResult)

        val expData = byteArrayOf()
        val actualSendMessageResult = messenger.sendMessage("nodeId", "/path", expData)
        assertThat(actualSendMessageResult.status.isSuccess).isTrue()
        verify(messageSender).sendMessage(eq("nodeId"), eq("/path"), same(expData))
    }

    @Test
    fun obtainMessage_success() = runBlocking<Unit> {
        val expMessageEvent = mock<MessageEvent>()
        val expMessageHandler = whenever(mock<MessageEventHandler>().obtain()).thenReturn(
                expMessageEvent).getMock() as MessageEventHandler
        whenever(eventHandlerFactory.create(any())).thenReturn(expMessageHandler)
        whenever(messageSender.addListener(any())).thenReturn(Status(CommonStatusCodes.SUCCESS))

        // FIXME: Can not spy messenger.
        val node = mock<Node> { on { id } doReturn "node" }
        val expGetConnectedNodeResult = mock<GetConnectedNodesResult> {
            on { nodes } doReturn listOf(node)
            on { status } doReturn Status(CommonStatusCodes.SUCCESS)
        }
        whenever(messageSender.getConnectedNodes()).thenReturn(expGetConnectedNodeResult)
        whenever(excludeNode.invoke(any())).thenReturn(false)

        val expSendMessageResult = mock<SendMessageResult> {
            on { status } doReturn Status(CommonStatusCodes.SUCCESS)
        }
        whenever(messageSender.sendMessage(anyString(), anyString(), any()))
                .thenReturn(expSendMessageResult)

        val expPaths = setOf("/exp_path")
        val expData = byteArrayOf()
        val actualMessageEvent = messenger.obtainMessage("/path", expData, expPaths)
        assertThat(actualMessageEvent).isSameAs(expMessageEvent)

        verify(eventHandlerFactory).create(same(expPaths))
        verify(messageSender).addListener(expMessageHandler)
        verify(messageSender).sendMessage(eq("node"), eq("/path"), same(expData))
        verify(messageSender).removeListener(expMessageHandler)
    }

    @Test
    fun obtainMessage_failedToAddListener() = runBlocking<Unit> {
        val expMessageHandler = mock<MessageEventHandler>()
        whenever(eventHandlerFactory.create(any())).thenReturn(expMessageHandler)

        val expErrorStatus = Status(CommonStatusCodes.ERROR)
        whenever(messageSender.addListener(any())).thenReturn(expErrorStatus)

        try {
            messenger.obtainMessage("/path", byteArrayOf(), setOf("/path"))
            fail("error!")
        } catch (e: ObtainMessageException) {
            assertThat(e.error).isSameAs(expErrorStatus)
        }

        verify(messageSender).addListener(expMessageHandler)
        verify(messageSender, never()).sendMessage(anyString(), anyString(), any())
        verify(expMessageHandler, never()).obtain()
        verify(messageSender, never()).removeListener(expMessageHandler)
    }

    @Test
    fun obtainMessage_failedToSendMessage() = runBlocking<Unit> {
        val expMessageEvent = mock<MessageEvent>()
        val expMessageHandler = whenever(mock<MessageEventHandler>().obtain()).thenReturn(
                expMessageEvent).getMock() as MessageEventHandler
        whenever(eventHandlerFactory.create(any())).thenReturn(expMessageHandler)
        whenever(messageSender.addListener(any())).thenReturn(Status(CommonStatusCodes.SUCCESS))

        // FIXME: Can not spy messenger.
        val expErrorStatus = Status(CommonStatusCodes.ERROR)
        val node = mock<Node> { on { id } doReturn "node" }
        val expGetConnectedNodeResult = mock<GetConnectedNodesResult> {
            on { nodes } doReturn listOf(node)
            on { status } doReturn expErrorStatus
        }
        whenever(messageSender.getConnectedNodes()).thenReturn(expGetConnectedNodeResult)

        try {
            messenger.obtainMessage("/path", byteArrayOf(), setOf("/path"))
            fail("error!")
        } catch (e: ObtainMessageException) {
            assertThat(e.error).isSameAs(expErrorStatus)
        }
        verify(messageSender).addListener(expMessageHandler)
        verify(messageSender, never()).sendMessage(anyString(), anyString(), any())
        verify(expMessageHandler, never()).obtain()
        verify(messageSender).removeListener(expMessageHandler)
    }

    @Test
    fun obtainMessage_specifyNodeId_success() = runBlocking<Unit> {
        val expMessageEvent = mock<MessageEvent>()
        val expMessageHandler = whenever(mock<MessageEventHandler>().obtain()).thenReturn(
                expMessageEvent).getMock() as MessageEventHandler
        whenever(eventHandlerFactory.create(any())).thenReturn(expMessageHandler)
        whenever(messageSender.addListener(any())).thenReturn(Status(CommonStatusCodes.SUCCESS))

        // FIXME: Can not spy messenger.
        val expSendMessageResult = mock<SendMessageResult> {
            on { status } doReturn Status(CommonStatusCodes.SUCCESS)
        }
        whenever(messageSender.sendMessage(anyString(), anyString(), any()))
                .thenReturn(expSendMessageResult)

        val expPaths = setOf("/exp_path")
        val expData = byteArrayOf()
        val actualMessageEvent = messenger.obtainMessage("node", "/path", expData, expPaths)
        assertThat(actualMessageEvent).isSameAs(expMessageEvent)

        verify(eventHandlerFactory).create(same(expPaths))
        verify(messageSender).addListener(expMessageHandler)
        verify(messageSender).sendMessage(eq("node"), eq("/path"), same(expData))
        verify(messageSender).removeListener(expMessageHandler)
    }

    @Test
    fun obtainMessage_specifyNodeId_failedToAddListener() = runBlocking<Unit> {
        val expMessageHandler = mock<MessageEventHandler>()
        whenever(eventHandlerFactory.create(any())).thenReturn(expMessageHandler)

        val expErrorStatus = Status(CommonStatusCodes.ERROR)
        whenever(messageSender.addListener(any())).thenReturn(expErrorStatus)

        try {
            messenger.obtainMessage("nodeId", "/path", byteArrayOf(), setOf("/path"))
            fail("error!")
        } catch (e: ObtainMessageException) {
            assertThat(e.error).isSameAs(expErrorStatus)
        }

        verify(messageSender).addListener(expMessageHandler)
        verify(messageSender, never()).sendMessage(anyString(), anyString(), any())
        verify(expMessageHandler, never()).obtain()
        verify(messageSender, never()).removeListener(expMessageHandler)
    }

    @Test
    fun obtainMessage_specifyNodeId_failedToSendMessage() = runBlocking<Unit> {
        val expMessageHandler = mock<MessageEventHandler>()
        whenever(eventHandlerFactory.create(any())).thenReturn(expMessageHandler)
        whenever(messageSender.addListener(any())).thenReturn(Status(CommonStatusCodes.SUCCESS))

        // FIXME: Can not spy messenger.
        val expErrorStatus = Status(CommonStatusCodes.ERROR)
        val expSendMessageResult = mock<SendMessageResult> {
            on { status } doReturn expErrorStatus
        }
        whenever(messageSender.sendMessage(anyString(), anyString(), any()))
                .thenReturn(expSendMessageResult)

        val expData = byteArrayOf()
        try {
            messenger.obtainMessage("node", "/path", expData, setOf("/path"))
            fail("error!")
        } catch (e: ObtainMessageException) {
            assertThat(e.error).isSameAs(expErrorStatus)
        }

        verify(messageSender).addListener(expMessageHandler)
        verify(messageSender).sendMessage(eq("node"), eq("/path"), same(expData))
        verify(expMessageHandler, never()).obtain()
        verify(messageSender).removeListener(expMessageHandler)
    }
}