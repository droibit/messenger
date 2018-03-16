package com.github.droibit.messenger.internal

import com.github.droibit.messenger.internal.WearableClient.ClientProvider
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.nullable
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit

private typealias Nodes = List<Node>

@Suppress("UNCHECKED_CAST")
class WearableClientImplTest {

  @get:Rule
  val rule = MockitoJUnit.rule()

  @Mock
  private lateinit var clientProvider: ClientProvider

  @Test
  fun getConnectedNodes_completed() = runBlocking<Unit> {
    val expectedNodes = mock<Nodes>()
    val mockTask = mock<Task<Nodes>> {
      on { isSuccessful } doReturn true
      on { result } doReturn expectedNodes
    }
    whenever(mockTask.addOnCompleteListener(any())).thenAnswer {
      (it.arguments[0] as OnCompleteListener<Nodes>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockNodeClient = mock<NodeClient> {
      on { connectedNodes } doReturn mockTask
    }
    whenever(clientProvider.nodeClient).thenReturn(mockNodeClient)

    val newClient = newClient(getNodesTimeout = 100L)
    val actualNodes = newClient.getConnectedNodes()
    assertThat(actualNodes).isSameAs(expectedNodes)
  }

  @Test
  fun getConnectedNodes_error() = runBlocking<Unit> {
    val mockTask = mock<Task<Nodes>> {
      on { isSuccessful } doReturn false
      on { exception } doReturn mock<ApiException>()
    }
    whenever(mockTask.addOnCompleteListener(any())).thenAnswer {
      (it.arguments[0] as OnCompleteListener<Nodes>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockNodeClient = mock<NodeClient> {
      on { connectedNodes } doReturn mockTask
    }
    whenever(clientProvider.nodeClient).thenReturn(mockNodeClient)

    try {
      newClient(getNodesTimeout = 100L).getConnectedNodes()
      fail("error!")
    } catch (e: Exception) {
      assertThat(e).isExactlyInstanceOf(ApiException::class.java)
    }
  }

  @Test
  fun getConnectedNodes_cancel() = runBlocking<Unit> {
    val mockTask = mock<Task<Nodes>>()
    val mockNodeClient = mock<NodeClient> {
      on { connectedNodes } doReturn mockTask
    }
    whenever(clientProvider.nodeClient).thenReturn(mockNodeClient)

    val job = launch {
      try {
        newClient(getNodesTimeout = 100L).getConnectedNodes()
        fail("error!")
      } catch (e: Exception) {
        assertThat(e).isExactlyInstanceOf(CancellationException::class.java)
      }
    }
    delay(50L)
    job.cancelAndJoin()

    val captor = argumentCaptor<CompleteEventHandler<Nodes>>()
    verify(mockTask).addOnCompleteListener(captor.capture())
    assertThat(captor.firstValue.raw.get()).isNull()
  }

  @Test
  fun getCapability_completed() = runBlocking<Unit> {
    val expectedCapabilityInfo = mock<CapabilityInfo>()
    val mockTask = mock<Task<CapabilityInfo>> {
      on { isSuccessful } doReturn true
      on { result } doReturn expectedCapabilityInfo
    }
    whenever(mockTask.addOnCompleteListener(any())).thenAnswer {
      (it.arguments[0] as OnCompleteListener<CapabilityInfo>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockCapabilityClient = mock<CapabilityClient> {
      on { getCapability(anyString(), anyInt()) } doReturn mockTask
    }
    whenever(clientProvider.capabilityClient).thenReturn(mockCapabilityClient)

    val newClient = newClient(getNodesTimeout = 100L)
    val actualCapabilityInfo = newClient.getCapability("test", CapabilityClient.FILTER_REACHABLE)
    assertThat(actualCapabilityInfo).isSameAs(expectedCapabilityInfo)
  }

  @Test
  fun getCapability_error() = runBlocking<Unit> {
    val mockTask = mock<Task<CapabilityInfo>> {
      on { isSuccessful } doReturn false
      on { exception } doReturn mock<ApiException>()
    }
    whenever(mockTask.addOnCompleteListener(any())).thenAnswer {
      (it.arguments[0] as OnCompleteListener<CapabilityInfo>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockCapabilityClient = mock<CapabilityClient> {
      on { getCapability(anyString(), anyInt()) } doReturn mockTask
    }
    whenever(clientProvider.capabilityClient).thenReturn(mockCapabilityClient)

    try {
      newClient(getNodesTimeout = 100L).getCapability("test", CapabilityClient.FILTER_REACHABLE)
      fail("error!")
    } catch (e: Exception) {
      assertThat(e).isExactlyInstanceOf(ApiException::class.java)
    }
  }

  @Test
  fun getCapability_cancel() = runBlocking<Unit> {
    val mockTask = mock<Task<CapabilityInfo>>()
    val mockCapabilityClient = mock<CapabilityClient> {
      on { getCapability(anyString(), anyInt()) } doReturn mockTask
    }
    whenever(clientProvider.capabilityClient).thenReturn(mockCapabilityClient)

    val job = launch {
      try {
        newClient(getNodesTimeout = 100L)
            .getCapability("test", CapabilityClient.FILTER_REACHABLE)
        fail("error!")
      } catch (e: Exception) {
        assertThat(e).isExactlyInstanceOf(CancellationException::class.java)
      }
    }
    delay(50L)
    job.cancelAndJoin()

    val captor = argumentCaptor<CompleteEventHandler<CapabilityInfo>>()
    verify(mockTask).addOnCompleteListener(captor.capture())
    assertThat(captor.firstValue.raw.get()).isNull()
  }

  @Test
  fun sendMessage_completed() = runBlocking<Unit> {
    val expectedRequestId = 1
    val mockTask = mock<Task<Int>> {
      on { isSuccessful } doReturn true
      on { result } doReturn expectedRequestId
    }
    whenever(mockTask.addOnCompleteListener(any())).thenAnswer {
      (it.arguments[0] as OnCompleteListener<Int>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockMessageClient = mock<MessageClient> {
      on {
        sendMessage(
            anyString(), anyString(), nullable(ByteArray::class.java)
        )
      } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    val newClient = newClient(sendMessageTimeout = 100L)
    val actualRequestId = newClient.sendMessage("node1", "/path", null)
    assertThat(actualRequestId).isEqualTo(expectedRequestId)
  }

  @Test
  fun sendMessage_error() = runBlocking<Unit> {
    val mockTask = mock<Task<Int>> {
      on { isSuccessful } doReturn false
      on { exception } doReturn mock<ApiException>()
    }
    whenever(mockTask.addOnCompleteListener(any())).thenAnswer {
      (it.arguments[0] as OnCompleteListener<Int>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockMessageClient = mock<MessageClient> {
      on {
        sendMessage(
            anyString(), anyString(), nullable(ByteArray::class.java)
        )
      } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    try {
      newClient(sendMessageTimeout = 100L).sendMessage("node1", "/path", null)
      fail("error!")
    } catch (e: Exception) {
      assertThat(e).isExactlyInstanceOf(ApiException::class.java)
    }
  }

  @Test
  fun sendMessage_cancel() = runBlocking<Unit> {
    val mockTask = mock<Task<Int>>()
    val mockMessageClient = mock<MessageClient> {
      on {
        sendMessage(anyString(), anyString(), nullable(ByteArray::class.java))
      } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    val job = launch {
      try {
        newClient(sendMessageTimeout = 100L).sendMessage("node1", "/path", null)
        fail("error!")
      } catch (e: Exception) {
        assertThat(e).isExactlyInstanceOf(CancellationException::class.java)
      }
    }
    delay(50L)
    job.cancelAndJoin()

    val captor = argumentCaptor<CompleteEventHandler<Int>>()
    verify(mockTask).addOnCompleteListener(captor.capture())
    assertThat(captor.firstValue.raw.get()).isNull()
  }

  @Test
  fun addListener_completed() = runBlocking<Unit> {
    val mockTask = mock<Task<Void>> {
      on { isSuccessful } doReturn true
    }
    whenever(mockTask.addOnCompleteListener(any())).thenAnswer {
      (it.arguments[0] as OnCompleteListener<Void>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockMessageClient = mock<MessageClient> {
      on { addListener(any()) } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    newClient(addListenerTimeout = 100L)
        .addListener(MessageClient.OnMessageReceivedListener {
        })
  }

  @Test
  fun addListener_error() = runBlocking<Unit> {
    val mockTask = mock<Task<Void>> {
      on { isSuccessful } doReturn false
      on { exception } doReturn mock<ApiException>()
    }
    whenever(mockTask.addOnCompleteListener(any())).thenAnswer {
      (it.arguments[0] as OnCompleteListener<Void>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockMessageClient = mock<MessageClient> {
      on { addListener(any()) } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    try {
      newClient(addListenerTimeout = 100L).addListener(MessageClient.OnMessageReceivedListener {})
      fail("error!")
    } catch (e: Exception) {
      assertThat(e).isExactlyInstanceOf(ApiException::class.java)
    }
  }

  @Test
  fun addListener_cancel() = runBlocking<Unit> {
    val mockTask = mock<Task<Void>>()
    val mockMessageClient = mock<MessageClient> {
      on { addListener(any()) } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    val job = launch {
      try {
        newClient(addListenerTimeout = 100L).addListener(MessageClient.OnMessageReceivedListener {})
        fail("error!")
      } catch (e: Exception) {
        assertThat(e).isExactlyInstanceOf(CancellationException::class.java)
      }
    }
    delay(50L)
    job.cancelAndJoin()

    val captor = argumentCaptor<CompleteEventHandler<Void>>()
    verify(mockTask).addOnCompleteListener(captor.capture())
    assertThat(captor.firstValue.raw.get()).isNull()
  }

  @Test
  fun removeListener_completed() = runBlocking<Unit> {
    val mockTask = mock<Task<Boolean>> {
      on { isSuccessful } doReturn true
      on { result } doReturn true
    }
    whenever(mockTask.addOnCompleteListener(any())).thenAnswer {
      (it.arguments[0] as OnCompleteListener<Boolean>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockMessageClient = mock<MessageClient> {
      on { removeListener(any()) } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    val actualResult = newClient(addListenerTimeout = 100L)
        .removeListener(MessageClient.OnMessageReceivedListener {
        })
    assertThat(actualResult).isTrue()
  }

  @Test
  fun removeListener_error() = runBlocking<Unit> {
    val mockTask = mock<Task<Boolean>> {
      on { isSuccessful } doReturn false
      on { exception } doReturn mock<ApiException>()
    }
    whenever(mockTask.addOnCompleteListener(any())).thenAnswer {
      (it.arguments[0] as OnCompleteListener<Boolean>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockMessageClient = mock<MessageClient> {
      on { removeListener(any()) } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    try {
      newClient(addListenerTimeout = 100L)
          .removeListener(MessageClient.OnMessageReceivedListener {})
      fail("error!")
    } catch (e: Exception) {
      assertThat(e).isExactlyInstanceOf(ApiException::class.java)
    }
  }

  private fun newClient(
    getNodesTimeout: Long = 0,
    sendMessageTimeout: Long = 0,
    addListenerTimeout: Long = 0
  ) = WearableClientImpl(
      clientProvider,
      getNodesTimeout,
      sendMessageTimeout,
      addListenerTimeout
  )
}