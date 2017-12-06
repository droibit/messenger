@file:Suppress("FunctionName")

package com.github.droibit.messenger.internal

import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageApi.SendMessageResult
import com.google.android.gms.wearable.NodeApi
import com.google.android.gms.wearable.NodeApi.GetConnectedNodesResult
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.anyVararg
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.assertj.core.api.Java6Assertions.assertThat
import org.assertj.core.api.Java6Assertions.fail
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.same
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import java.util.concurrent.TimeUnit

private typealias PendingGetConnectedNodesResult = PendingResult<GetConnectedNodesResult>
private typealias PendingSendMessageResult = PendingResult<SendMessageResult>
private typealias PendingStatus = PendingResult<Status>

class SuspendMessageSenderImplTest {

    @Rule
    @JvmField
    val rule = MockitoJUnit.rule()

    @Mock
    private lateinit var apiClient: GoogleApiClient

    @Mock
    private lateinit var nodeApi: NodeApi

    @Mock
    private lateinit var messageApi: MessageApi

    @Suppress("UNCHECKED_CAST")
    @Test
    fun getConnectedNodes() = runBlocking {
        try {
            val expConnectedNodes = mock<GetConnectedNodesResult>()
            val expConnectedNodesResult = mock<PendingGetConnectedNodesResult> {
                on { setResultCallback(any(), anyLong(), any()) } doAnswer {
                    val resultCallback = it.arguments[0] as ResultCallback<GetConnectedNodesResult>
                    resultCallback.onResult(expConnectedNodes)
                }
            }
            whenever(nodeApi.getConnectedNodes(any())).doReturn(expConnectedNodesResult)

            val sender = newSender(getConnectNodesTimeout = 100L)
            val actualConnectedNodes = sender.getConnectedNodes()
            assertThat(actualConnectedNodes).isSameAs(expConnectedNodes)

            verify(expConnectedNodesResult).setResultCallback(any(), eq(100L),
                    eq(TimeUnit.MILLISECONDS))
        } catch (e: CancellationException) {
            fail(e.message)
        }
    }

    @Test
    fun getConnectedNodes_cancel() = runBlocking {
        val expConnectedNodesResult = mock<PendingGetConnectedNodesResult> {
            on { isCanceled } doReturn false
        }
        whenever(nodeApi.getConnectedNodes(any())).doReturn(expConnectedNodesResult)

        val job = launch {
            try {
                val sender = newSender(getConnectNodesTimeout = 100L)
                sender.getConnectedNodes()
                fail("error")
            } catch (e: Exception) {
                assertThat(e).isExactlyInstanceOf(CancellationException::class.java)
            }
        }

        delay(50L)
        job.cancel()

        verify(expConnectedNodesResult).cancel()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun sendMessage() = runBlocking {
        try {
            val expSendMessage = mock<SendMessageResult>()
            val expSendMessageResult = mock<PendingSendMessageResult> {
                on { setResultCallback(any(), anyLong(), any()) } doAnswer {
                    val resultCallback = it.arguments[0] as ResultCallback<SendMessageResult>
                    resultCallback.onResult(expSendMessage)
                }
            }
            whenever(messageApi.sendMessage(any(), anyString(), anyString(), anyVararg())).doReturn(
                    expSendMessageResult)

            val sender = newSender(sendMessageTimeout = 100L)
            val expData = byteArrayOf()
            val actualSendMessage = sender.sendMessage("nodeId", "/path", expData)
            assertThat(actualSendMessage).isSameAs(expSendMessage)

            verify(messageApi).sendMessage(any(), eq("nodeId"), eq("/path"), same(expData))
            verify(expSendMessageResult).setResultCallback(any(), eq(100L),
                    eq(TimeUnit.MILLISECONDS))
        } catch (e: CancellationException) {
            fail(e.message)
        }
    }

    @Test
    fun sendMessage_cancel() = runBlocking {
        val expSendMessageResult = mock<PendingSendMessageResult> {
            on { isCanceled } doReturn false
        }
        whenever(messageApi.sendMessage(any(), anyString(), anyString(), anyVararg())).doReturn(
                expSendMessageResult)

        val job = launch {
            try {
                val sender = newSender(getConnectNodesTimeout = 100L)
                sender.sendMessage("nodeId", "/path", byteArrayOf())
                fail("error")
            } catch (e: Exception) {
                assertThat(e).isExactlyInstanceOf(CancellationException::class.java)
            }
        }

        delay(50L)
        job.cancel()

        verify(expSendMessageResult).cancel()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun addListener() = runBlocking {
        try {
            val expAddListener = Status(CommonStatusCodes.SUCCESS)
            val expAddListenerResult = mock<PendingStatus> {
                on { setResultCallback(any(), anyLong(), any()) } doAnswer {
                    val resultCallback = it.arguments[0] as ResultCallback<Status>
                    resultCallback.onResult(expAddListener)
                }
            }
            whenever(messageApi.addListener(any(), any())).doReturn(expAddListenerResult)

            val expMessageListener = mock<MessageApi.MessageListener>()
            val sender = newSender()
            val actualAddListener = sender.addListener(expMessageListener)
            assertThat(actualAddListener).isSameAs(expAddListener)

            verify(messageApi).addListener(any(), same(expMessageListener))
            verify(expAddListenerResult).setResultCallback(any(), eq(ADD_LISTENER_TIMEOUT_MILLIS),
                    eq(TimeUnit.MILLISECONDS))
        } catch (e: CancellationException) {
            fail(e.message)
        }
    }

    @Test
    fun addListener_cancel() = runBlocking {
        val expAddListenerResult= mock<PendingStatus> {
            on { isCanceled } doReturn false
        }
        whenever(messageApi.addListener(any(), any())).doReturn(expAddListenerResult)

        val job = launch {
            try {
                val sender = newSender()
                sender.addListener(mock())
                fail("error")
            } catch (e: Exception) {
                assertThat(e).isExactlyInstanceOf(CancellationException::class.java)
            }
        }

        delay(50L)
        job.cancel()

        verify(expAddListenerResult).cancel()
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun removeListener() = runBlocking {
        try {
            val expRemoveListener = Status(CommonStatusCodes.SUCCESS)
            val expRemoveListenerResult = mock<PendingStatus> {
                on { setResultCallback(any()) } doAnswer {
                    val resultCallback = it.arguments[0] as ResultCallback<Status>
                    resultCallback.onResult(expRemoveListener)
                }
            }
            whenever(messageApi.removeListener(any(), any())).doReturn(expRemoveListenerResult)

            val expMessageListener = mock<MessageApi.MessageListener>()
            val sender = newSender()
            val actualRemoveListener = sender.removeListener(expMessageListener)
            assertThat(actualRemoveListener).isSameAs(expRemoveListener)

            verify(messageApi).removeListener(any(), same(expMessageListener))
            verify(expRemoveListenerResult).setResultCallback(any())
        } catch (e: CancellationException) {
            fail(e.message)
        }
    }

    private fun newSender(getConnectNodesTimeout: Long = 0L, sendMessageTimeout: Long = 0L)
            : SuspendMessageSenderImpl = SuspendMessageSenderImpl(apiClient, nodeApi, messageApi,
            getConnectNodesTimeout, sendMessageTimeout)
}