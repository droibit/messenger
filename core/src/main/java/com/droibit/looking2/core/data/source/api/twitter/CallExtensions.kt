package com.droibit.looking2.core.data.source.api.twitter

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun <T> Call<T>.executeInternal(): T {
    return suspendCancellableCoroutine { context ->
        enqueue(object : Callback<T>() {
            override fun success(result: Result<T>) {
                context.resume(result.data)
            }

            override fun failure(exception: TwitterException) {
                if (context.isActive) context.resumeWithException(exception)
            }
        })

        context.invokeOnCancellation {
            if (!isCanceled) cancel()
        }
    }
}