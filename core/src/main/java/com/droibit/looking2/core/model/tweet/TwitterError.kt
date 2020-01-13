package com.droibit.looking2.core.model.tweet

import androidx.work.ListenableWorker
import com.twitter.sdk.android.core.TwitterApiException
import java.io.IOException
import androidx.work.ListenableWorker.Result as WorkResult

private const val STATUS_CODE_UNAUTHORIZED = 401
private const val STATUS_CODE_TOO_MANY_ACCESS = 429

private const val DEFAULT_MAX_RUN_ATTEMPT_COUNT = 2

// TODO: Consider adding auth error & limited api error.
sealed class TwitterError(message: String? = null) : Exception(message) {
    class Network : TwitterError()
    class Limited : TwitterError()
    object Unauthorized : TwitterError()
    class UnExpected(val errorCode: Int = Int.MIN_VALUE) : TwitterError()
}

fun Exception.toTwitterError(): TwitterError {
    if (this.cause is IOException) return TwitterError.Network()
    if (this !is TwitterApiException) return TwitterError.UnExpected()

    return when (this.statusCode) {
        STATUS_CODE_UNAUTHORIZED -> TwitterError.Unauthorized
        STATUS_CODE_TOO_MANY_ACCESS -> TwitterError.Limited()
        else -> TwitterError.UnExpected(errorCode = this.errorCode)
    }
}

fun ListenableWorker.retryIfNeeded(
    cause: TwitterError,
    maxRunAttemptCount: Int = DEFAULT_MAX_RUN_ATTEMPT_COUNT
): WorkResult {
    return when (cause) {
        is TwitterError.Network -> {
            if (runAttemptCount < maxRunAttemptCount) {
                WorkResult.retry()
            } else {
                WorkResult.failure()
            }
        }
        else -> WorkResult.failure()
    }
}