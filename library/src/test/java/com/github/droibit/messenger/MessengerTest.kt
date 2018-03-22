package com.github.droibit.messenger

import com.github.droibit.messenger.internal.MessageEventHandler
import com.github.droibit.messenger.internal.WearableClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.WearableStatusCodes
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.same
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.nullable
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

class MessengerTest {

  @get:Rule
  val rule = MockitoJUnit.rule()

  @Mock
  private lateinit var wearableClient: WearableClient

  @Mock
  private lateinit var messageHandlerFactory: MessageEventHandler.Factory

  @Mock
  private lateinit var excludeNode: ExcludeNode

  @InjectMocks
  private lateinit var messenger: Messenger

  @Test fun sendMessage_success() = runBlocking<Unit> {
    whenever(excludeNode.invoke(any()))
        .thenReturn(true)
        .thenReturn(false)

    val node1 = mock<Node>()
    val node2 = mock<Node> { on { id } doReturn "node2" }
    whenever(wearableClient.getConnectedNodes()).thenReturn(listOf(node1, node2))

    messenger.sendMessage("/path", byteArrayOf())

    verify(wearableClient).sendMessage(eq(node2.id), any(), any())
  }

  @Test(expected = ApiException::class)
  fun sendMessage_failedToGetConnectedNodes() = runBlocking<Unit> {
    whenever(wearableClient.getConnectedNodes()).thenThrow(ApiException::class.java)

    messenger.sendMessage("/path", byteArrayOf())
    fail("error")
  }

  @Test(expected = ApiException::class)
  fun sendMessage_failedToSendMessage() = runBlocking<Unit> {
    whenever(excludeNode.invoke(any())).thenReturn(false)

    val node1 = mock<Node> { on { id } doReturn "id" }
    whenever(wearableClient.getConnectedNodes()).thenReturn(listOf(node1))
    whenever(wearableClient.sendMessage(anyString(), anyString(), nullable(ByteArray::class.java)))
        .thenThrow(ApiException::class.java)

    messenger.sendMessage("/path", byteArrayOf())
    fail("error")
  }

  @Test(expected = CancellationException::class)
  fun sendMessage_canceled() = runBlocking<Unit> {
    whenever(excludeNode.invoke(any())).thenReturn(false)

    val node1 = mock<Node> { on { id } doReturn "id" }
    whenever(wearableClient.getConnectedNodes()).thenReturn(listOf(node1))
    whenever(wearableClient.sendMessage(anyString(), anyString(), nullable(ByteArray::class.java)))
        .thenThrow(CancellationException::class.java)

    messenger.sendMessage("/path", byteArrayOf())
    fail("error")
  }

  @Test(expected = ApiException::class)
  fun sendMessage_strictSend() = runBlocking<Unit> {
    whenever(excludeNode.invoke(any())).thenReturn(true)

    val node1 = mock<Node> { on { id } doReturn "id1" }
    val node2 = mock<Node> { on { id } doReturn "id2" }
    whenever(wearableClient.getConnectedNodes()).thenReturn(listOf(node1, node2))

    messenger.sendMessage("/path", byteArrayOf(), strictSend = true)
    fail("error")
  }

  @Test
  fun sendMessage_hasNodeId() = runBlocking<Unit> {
    val expectedNodeId = "nodeId"
    val expectedPath = "/path"
    val expectedData = byteArrayOf()
    messenger.sendMessage(expectedNodeId, expectedPath, expectedData)

    verify(wearableClient).sendMessage(eq(expectedNodeId), eq(expectedPath), same(expectedData))
  }

  @Test
  fun obtainMessage_success() = runBlocking<Unit> {
    whenever(excludeNode.invoke(any()))
        .thenReturn(true)
        .thenReturn(false)

    val node1 = mock<Node>()
    val node2 = mock<Node> { on { id } doReturn "node2" }
    whenever(wearableClient.getConnectedNodes()).thenReturn(listOf(node1, node2))

    val expectedMessageEvent = mock<MessageEvent>()
    val expectedMessageHandler = whenever(mock<MessageEventHandler>().obtain())
        .thenReturn(expectedMessageEvent)
        .getMock() as MessageEventHandler
    whenever(messageHandlerFactory.create(any())).thenReturn(expectedMessageHandler)

    val expectedPaths = setOf("/exp_path")
    val expectedData = byteArrayOf()
    val actualMessageEvent = messenger.obtainMessage("/path", expectedData, expectedPaths)
    assertThat(actualMessageEvent).isSameAs(expectedMessageEvent)

    verify(messageHandlerFactory).create(same(expectedPaths))
    verify(wearableClient).addListener(expectedMessageHandler)
    verify(wearableClient).sendMessage(eq(node2.id), eq("/path"), same(expectedData))
    verify(wearableClient).removeListener(expectedMessageHandler)
  }

  @Test
  fun obtainMessage_hasNodeId_success() = runBlocking<Unit> {
    val expectedMessageEvent = mock<MessageEvent>()
    val expectedMessageHandler = whenever(mock<MessageEventHandler>().obtain())
        .thenReturn(expectedMessageEvent)
        .getMock() as MessageEventHandler
    whenever(messageHandlerFactory.create(any())).thenReturn(expectedMessageHandler)

    val expectedNodeId = "node"
    val expectedPaths = setOf("/exp_path")
    val expectedData = byteArrayOf()
    val actualMessageEvent =
      messenger.obtainMessage(expectedNodeId, "/path", expectedData, expectedPaths)
    assertThat(actualMessageEvent).isSameAs(expectedMessageEvent)

    verify(messageHandlerFactory).create(same(expectedPaths))
    verify(wearableClient).addListener(expectedMessageHandler)
    verify(wearableClient).sendMessage(eq(expectedNodeId), eq("/path"), same(expectedData))
    verify(wearableClient).removeListener(expectedMessageHandler)
  }

  @Test(expected = ApiException::class)
  fun obtainMessage_failedToGetConnectedNodes() = runBlocking<Unit> {
    whenever(wearableClient.getConnectedNodes()).thenThrow(ApiException::class.java)

    messenger.obtainMessage("/path", byteArrayOf(), setOf("/path"))
    fail("error")
  }

  @Test
  fun obtainMessage_excludeNode() = runBlocking<Unit> {
    whenever(excludeNode.invoke(any())).thenReturn(true)

    val node1 = mock<Node>()
    val node2 = mock<Node>()
    whenever(wearableClient.getConnectedNodes()).thenReturn(listOf(node1, node2))

    try {
      messenger.obtainMessage("/path", byteArrayOf(), setOf("/path"))
    } catch (e: ApiException) {
      assertThat(e.statusCode).isEqualTo(WearableStatusCodes.TARGET_NODE_NOT_CONNECTED)
    }
  }

  @Test(expected = ApiException::class)
  fun obtainMessage_failedToAddListener() = runBlocking<Unit> {
    whenever(excludeNode.invoke(any())).thenReturn(false)

    val node1 = mock<Node> { on { id } doReturn "id" }
    whenever(wearableClient.getConnectedNodes()).thenReturn(listOf(node1))

    val expectedMessageHandler = mock<MessageEventHandler>()
    whenever(messageHandlerFactory.create(any())).thenReturn(expectedMessageHandler)

    doThrow(ApiException::class).whenever(wearableClient)
        .addListener(any())

    messenger.obtainMessage("/path", byteArrayOf(), setOf("/path"))
    fail("error")
  }

  @Test(expected = ApiException::class)
  fun obtainMessage_failedToSendMessage() = runBlocking<Unit> {
    whenever(excludeNode.invoke(any())).thenReturn(false)

    val node1 = mock<Node>() { on { id } doReturn "node" }
    whenever(wearableClient.getConnectedNodes()).thenReturn(listOf(node1))

    val expectedMessageEvent = mock<MessageEvent>()
    val expectedMessageHandler = whenever(mock<MessageEventHandler>().obtain())
        .thenReturn(expectedMessageEvent)
        .getMock() as MessageEventHandler
    whenever(messageHandlerFactory.create(any())).thenReturn(expectedMessageHandler)

    whenever(wearableClient.sendMessage(anyString(), anyString(), nullable(ByteArray::class.java)))
        .thenThrow(ApiException::class.java)

    messenger.obtainMessage("/path", byteArrayOf(), setOf("/path"))
    fail("error")
  }

  @Test
  fun getCapability_success() = runBlocking<Unit> {
    val expectedCapabilityInfo = mock<CapabilityInfo>()
    whenever(wearableClient.getCapability(anyString(), anyInt())).thenReturn(expectedCapabilityInfo)

    val actualCapabilityInfo = messenger.getCapability("test", CapabilityClient.FILTER_REACHABLE)
    assertThat(actualCapabilityInfo).isSameAs(expectedCapabilityInfo)
  }

  @Test(expected = ApiException::class)
  fun getCapability_error() = runBlocking<Unit> {
    whenever(wearableClient.getCapability(anyString(), anyInt()))
        .thenThrow(ApiException::class.java)

    messenger.getCapability("test", CapabilityClient.FILTER_REACHABLE)
  }

  @Test(expected = CancellationException::class)
  fun getCapability_cancel() = runBlocking<Unit> {
    whenever(wearableClient.getCapability(anyString(), anyInt()))
        .thenThrow(CancellationException::class.java)

    messenger.getCapability("test", CapabilityClient.FILTER_REACHABLE)
  }

  @Test
  fun getConnectedNodes_excludeNode() = runBlocking<Unit> {
    whenever(excludeNode.invoke(any()))
        .thenReturn(true)
        .thenReturn(false)

    val node1 = mock<Node>()
    val node2 = mock<Node>()
    whenever(wearableClient.getConnectedNodes()).thenReturn(listOf(node1, node2))

    val actualConnectedNodes = messenger.getConnectedNodes(useExcludeNode = true)
    assertThat(actualConnectedNodes).containsExactly(node2)
  }

  @Test
  fun getConnectedNodes_allNodes() = runBlocking<Unit> {
    whenever(excludeNode.invoke(any())).thenReturn(true)

    val node1 = mock<Node>()
    val node2 = mock<Node>()
    whenever(wearableClient.getConnectedNodes()).thenReturn(listOf(node1, node2))

    val actualConnectedNodes = messenger.getConnectedNodes(useExcludeNode = false)
    assertThat(actualConnectedNodes).containsExactlyInAnyOrder(node1, node2)
  }

  @Test(expected = ApiException::class)
  fun getConnectedNodes_error() = runBlocking<Unit> {
    whenever(wearableClient.getConnectedNodes()).thenThrow(ApiException::class.java)

    messenger.getConnectedNodes()
    fail("error")
  }

  @Test(expected = CancellationException::class)
  fun getConnectedNodes_cancel() = runBlocking<Unit> {
    whenever(wearableClient.getConnectedNodes()).thenThrow(CancellationException::class.java)

    messenger.getConnectedNodes()
    fail("error")
  }
}