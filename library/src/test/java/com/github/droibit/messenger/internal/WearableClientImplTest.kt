package com.github.droibit.messenger.internal

import app.cash.turbine.test
import com.github.droibit.messenger.internal.WearableClient.ClientProvider
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageClient.OnMessageReceivedListener
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.nullable
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.Executor

private typealias NodeList = List<Node>

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class WearableClientImplTest {

  @get:Rule
  val rule: MockitoRule = MockitoJUnit.rule()

  @Mock
  private lateinit var clientProvider: ClientProvider

  @Test
  fun messageEvents_notifyEvents() = runTest {
    val mockTask = mock<Task<Void>> {
      on { isSuccessful } doReturn true
    }
    lateinit var onCompleteListener: OnCompleteListener<Void>
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      onCompleteListener = (it.arguments[1] as OnCompleteListener<Void>)
      it.mock
    }

    val mockMessageClient = mock<MessageClient>()
    lateinit var onMessageReceivedListener: OnMessageReceivedListener
    whenever(mockMessageClient.addListener(any())).thenAnswer {
      onMessageReceivedListener = (it.arguments[0] as OnMessageReceivedListener)
      mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    newClient().messageEvents.test {
      val event1 = mock<MessageEvent>()
      onMessageReceivedListener.onMessageReceived(event1)
      assertThat(awaitItem()).isEqualTo(event1)

      val event2 = mock<MessageEvent>()
      onMessageReceivedListener.onMessageReceived(event2)
      assertThat(awaitItem()).isEqualTo(event2)

      onCompleteListener.onComplete(mockTask)

      cancelAndIgnoreRemainingEvents()
    }
    verify(mockMessageClient).removeListener(onMessageReceivedListener)
  }

  @Test
  fun messageEvents_notifyError() = runTest {
    val error = mock<ApiException>()
    val mockTask = mock<Task<Void>> {
      on { isSuccessful } doReturn false
      on { exception } doReturn error
    }

    lateinit var onCompleteListener: OnCompleteListener<Void>
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      onCompleteListener = (it.arguments[1] as OnCompleteListener<Void>)
      it.mock
    }

    val mockMessageClient = mock<MessageClient> {
      on { addListener(any()) } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    newClient().messageEvents.test {
      try {
        onCompleteListener.onComplete(mockTask)
        awaitError()
      } catch (e: Exception) {
        assertThat(e).isEqualTo(error)
      }
    }

    verify(mockMessageClient, never()).removeListener(any())
  }

  @Test
  fun getConnectedNodes_completed() = runTest {
    val expectedNodes = mock<NodeList>()
    val mockTask = mock<Task<NodeList>> {
      on { isComplete } doReturn true
      on { result } doReturn expectedNodes
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<NodeList>).apply {
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

  @Test(expected = ApiException::class)
  fun getConnectedNodes_error() = runTest {
    val mockTask = mock<Task<NodeList>> {
      on { isComplete } doReturn true
      on { exception } doReturn mock<ApiException>()
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<NodeList>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockNodeClient = mock<NodeClient> {
      on { connectedNodes } doReturn mockTask
    }
    whenever(clientProvider.nodeClient).thenReturn(mockNodeClient)

    newClient(getNodesTimeout = 100L).getConnectedNodes()
    fail("error!")
  }

  @Test(expected = CancellationException::class)
  fun getConnectedNodes_cancel() = runTest {
    val mockTask = mock<Task<NodeList>>() {
      on { isComplete } doReturn true
      on { isCanceled } doReturn true
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<NodeList>).apply {
        onComplete(mockTask)
      }
      mockTask
    }
    val mockNodeClient = mock<NodeClient> {
      on { connectedNodes } doReturn mockTask
    }
    whenever(clientProvider.nodeClient).thenReturn(mockNodeClient)

    newClient(getNodesTimeout = 100L).getConnectedNodes()
  }

  @Test
  fun getCapability_completed() = runTest {
    val expectedCapabilityInfo = mock<CapabilityInfo>()
    val mockTask = mock<Task<CapabilityInfo>> {
      on { isComplete } doReturn true
      on { result } doReturn expectedCapabilityInfo
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<CapabilityInfo>).apply {
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

  @Test(expected = ApiException::class)
  fun getCapability_error() = runTest {
    val mockTask = mock<Task<CapabilityInfo>> {
      on { isComplete } doReturn true
      on { exception } doReturn mock<ApiException>()
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<CapabilityInfo>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockCapabilityClient = mock<CapabilityClient> {
      on { getCapability(anyString(), anyInt()) } doReturn mockTask
    }
    whenever(clientProvider.capabilityClient).thenReturn(mockCapabilityClient)

    newClient(getNodesTimeout = 100L).getCapability("test", CapabilityClient.FILTER_REACHABLE)
  }

  @Test(expected = CancellationException::class)
  fun getCapability_cancel() = runTest {
    val mockTask = mock<Task<CapabilityInfo>> {
      on { isComplete } doReturn true
      on { isCanceled } doReturn true
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<CapabilityInfo>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockCapabilityClient = mock<CapabilityClient> {
      on { getCapability(anyString(), anyInt()) } doReturn mockTask
    }
    whenever(clientProvider.capabilityClient).thenReturn(mockCapabilityClient)

    newClient(getNodesTimeout = 100L).getCapability("test", CapabilityClient.FILTER_REACHABLE)
  }

  @Test
  fun sendMessage_completed() = runTest {
    val expectedRequestId = 1
    val mockTask = mock<Task<Int>> {
      on { isComplete } doReturn true
      on { result } doReturn expectedRequestId
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<Int>).apply {
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

  @Test(expected = ApiException::class)
  fun sendMessage_error() = runTest {
    val mockTask = mock<Task<Int>> {
      on { isComplete } doReturn true
      on { exception } doReturn mock<ApiException>()
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<Int>).apply {
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

    newClient(sendMessageTimeout = 100L).sendMessage("node1", "/path", null)
  }

  @Test(expected = CancellationException::class)
  fun sendMessage_cancel() = runTest {
    val mockTask = mock<Task<Int>> {
      on { isComplete } doReturn true
      on { isCanceled } doReturn true
    }
    whenever(mockTask.addOnCompleteListener(any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<Int>).apply {
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

    newClient(sendMessageTimeout = 100L).sendMessage("node1", "/path", null)
  }

  @Test
  fun addListener_completed() = runTest {
    val mockTask = mock<Task<Void>> {
      on { isComplete } doReturn true
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<Void>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockMessageClient = mock<MessageClient> {
      on { addListener(any()) } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    newClient().addListener {}
  }

  @Test(expected = ApiException::class)
  fun addListener_error() = runTest {
    val mockTask = mock<Task<Void>> {
      on { isComplete } doReturn true
      on { exception } doReturn mock<ApiException>()
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<Void>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockMessageClient = mock<MessageClient> {
      on { addListener(any()) } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    newClient().addListener {}
  }

  @Test(expected = CancellationException::class)
  fun addListener_cancel() = runTest {
    val mockTask = mock<Task<Void>>() {
      on { isComplete } doReturn true
      on { isCanceled } doReturn true
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<Void>).apply {
        onComplete(mockTask)
      }
      it.mock
    }

    val mockMessageClient = mock<MessageClient> {
      on { addListener(any()) } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    newClient().addListener {}
  }

  @Test
  fun removeListener_completed() = runTest {
    val mockTask = mock<Task<Boolean>> {
      on { isComplete } doReturn true
      on { result } doReturn true
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<Boolean>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockMessageClient = mock<MessageClient> {
      on { removeListener(any()) } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    val actualResult = newClient()
      .removeListener {}
    assertThat(actualResult).isTrue()
  }

  @Test(expected = ApiException::class)
  fun removeListener_error() = runTest {
    val mockTask = mock<Task<Boolean>> {
      on { isComplete } doReturn true
      on { exception } doReturn mock<ApiException>()
    }
    whenever(mockTask.addOnCompleteListener(any<Executor>(), any())).thenAnswer {
      (it.arguments[1] as OnCompleteListener<Boolean>).apply {
        onComplete(mockTask)
      }
      mockTask
    }

    val mockMessageClient = mock<MessageClient> {
      on { removeListener(any()) } doReturn mockTask
    }
    whenever(clientProvider.messageClient).thenReturn(mockMessageClient)

    newClient().removeListener {}
  }

  private fun newClient(
    getNodesTimeout: Long = 0,
    sendMessageTimeout: Long = 0,
  ) = WearableClientImpl(
    clientProvider,
    getNodesTimeout,
    sendMessageTimeout
  )
}