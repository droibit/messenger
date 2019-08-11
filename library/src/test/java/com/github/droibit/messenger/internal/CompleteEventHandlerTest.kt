package com.github.droibit.messenger.internal

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.tasks.Task
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.isA
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.CancellableContinuation

import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import kotlin.coroutines.resumeWithException

class CompleteEventHandlerTest {

  @get:Rule
  val rule = MockitoJUnit.rule()

  @Mock
  private lateinit var cont: CancellableContinuation<Unit>

  @InjectMocks
  private lateinit var listener: CompleteEventHandler<Unit>

  @Test
  fun onComplete_success() {
    val mockTask = mock<Task<Unit>> {
      on { isSuccessful } doReturn true
      on { result } doReturn Unit
    }

    listener.onComplete(mockTask)

    verify(cont).resume(Unit)
    verify(cont, never()).resumeWithException(any())
  }

  @Test
  fun oComplete_error_ApiException() {
    val mockTask = mock<Task<Unit>> {
      on { isSuccessful } doReturn false
      on { exception } doReturn mock<ApiException>()
    }

    listener.onComplete(mockTask)

    verify(cont).resumeWithException(isA<ApiException>())
    verify(cont, never()).resume(any())
  }

  @Test
  fun oComplete_error_unknown() {
    val mockTask = mock<Task<Unit>> {
      on { isSuccessful } doReturn false
      on { exception } doReturn mock<RuntimeException>()
    }

    listener.onComplete(mockTask)

    val captor = argumentCaptor<ApiException>()
    verify(cont).resumeWithException(captor.capture())
    verify(cont, never()).resume(any())

    assertThat(captor.firstValue.statusCode).isEqualTo(CommonStatusCodes.ERROR)
  }

  @Test
  fun cancel() {
    val mockTask = mock<Task<Unit>> {
      on { isSuccessful } doReturn true
      on { result } doReturn Unit
    }

    listener.cancel()
    listener.onComplete(mockTask)

    verify(cont, never()).resume(any())
    verify(cont, never()).resumeWithException(any())
  }
}