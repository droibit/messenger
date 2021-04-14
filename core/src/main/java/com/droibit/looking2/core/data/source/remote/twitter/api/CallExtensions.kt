package com.droibit.looking2.core.data.source.remote.twitter.api

import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call

@Throws(TwitterException::class)
internal suspend fun <T> Call<T>.await(): T {
    return suspendCancellableCoroutine { context ->
        enqueue(
            object : Callback<T>() {
                override fun success(result: Result<T>) {
                    if (context.isActive) context.resume(result.data)
                }

                override fun failure(exception: TwitterException) {
                    if (context.isActive) context.resumeWithException(exception)
                }
            }
        )

        context.invokeOnCancellation {
            cancel()
        }
    }
}
