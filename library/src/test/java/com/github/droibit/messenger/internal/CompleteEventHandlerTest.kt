package com.github.droibit.messenger.internal

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
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
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import kotlin.Result.Companion
import kotlin.coroutines.resumeWithException

class CompleteEventHandlerTest {

  @get:Rule
  val rule: MockitoRule = MockitoJUnit.rule()

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

    verify(cont).resumeWith(Result.success(Unit))
  }

  @Test
  fun oComplete_error_ApiException() {
    val mockApiException = mock<ApiException>()
    val mockTask = mock<Task<Unit>> {
      on { isSuccessful } doReturn false
      on { exception } doReturn mockApiException
    }

    listener.onComplete(mockTask)

    verify(cont).resumeWith(Result.failure(mockApiException))
  }

  @Ignore
  @Test
  fun oComplete_error_unknown() {
    val mockTask = mock<Task<Unit>> {
      on { isSuccessful } doReturn false
      on { exception } doReturn mock<RuntimeException>()
    }

    listener.onComplete(mockTask)

    val captor = argumentCaptor<Result<Unit>>()
    verify(cont).resumeWith(captor.capture())

    val actualException = captor.firstValue.exceptionOrNull()
    assertThat(actualException)
        .isNotNull()
        .isInstanceOf(ApiException::class.java)

    assertThat((actualException as ApiException).statusCode)
        .isEqualTo(CommonStatusCodes.ERROR)
  }

  @Ignore
  @Test
  fun cancel() {
    val mockTask = mock<Task<Unit>> {
      on { isSuccessful } doReturn true
      on { result } doReturn Unit
    }

    listener.cancel()
    listener.onComplete(mockTask)

    verify(cont, never()).resumeWith(any())
  }
}