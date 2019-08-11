package com.github.droibit.messenger.internal

import androidx.annotation.VisibleForTesting
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.lang.Exception
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.Continuation

private typealias Callback<T> = (Task<T>) -> Unit

internal class CompleteEventHandler<TResult : Any>(cont: Continuation<TResult>) :
    OnCompleteListener<TResult> {

  @VisibleForTesting
  internal var callback = AtomicReference<Callback<TResult>> {
    val result = if (it.isSuccessful) {
      Result.success(requireNotNull(it.result))
    } else {
      Result.failure(it.exception.asApiException())
    }
    cont.resumeWith(result)
  }

  override fun onComplete(task: Task<TResult>) {
      callback.get()?.invoke(task)
  }

  fun cancel() = callback.set(null)
}

internal class VoidCompleteEventHandler(cont: Continuation<Unit>) :
    OnCompleteListener<Void> {

  @VisibleForTesting
  internal var callback = AtomicReference<Callback<Void>> {
    val result = if (it.isSuccessful) {
      Result.success(Unit)
    } else {
      Result.failure(it.exception.asApiException())
    }
    cont.resumeWith(result)
  }

  override fun onComplete(task: Task<Void>) {
    callback.get()?.invoke(task)
  }

  fun cancel() = callback.set(null)
}

private fun Exception?.asApiException(): ApiException {
  return this as? ApiException ?: ApiException(Status(CommonStatusCodes.ERROR))
}