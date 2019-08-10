package com.github.droibit.messenger.internal

import android.support.annotation.VisibleForTesting
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private typealias Callback<T> = (Task<T>) -> Unit

internal class CompleteEventHandler<TResult>(cont: Continuation<TResult>) :
    OnCompleteListener<TResult> {

  @VisibleForTesting
  internal var raw = AtomicReference<Callback<TResult>> {
    if (it.isSuccessful) {
      cont.resume(requireNotNull(it.result))
      return@AtomicReference
    }

    val e = it.exception as? ApiException
    if (e == null) {
      cont.resumeWithException(ApiException(Status(CommonStatusCodes.ERROR)))
    } else {
      cont.resumeWithException(e)
    }
  }

  override fun onComplete(task: Task<TResult>) {
    val raw = this.raw.get()
    raw?.invoke(task)
  }

  fun cancel() = raw.set(null)
}