package com.droibit.looking2.core.model.tweet

import androidx.work.ListenableWorker
import com.twitter.sdk.android.core.TwitterApiException
import com.twitter.sdk.android.core.TwitterException
import timber.log.Timber
import java.io.IOException
import androidx.work.ListenableWorker.Result as WorkResult

private const val STATUS_CODE_UNAUTHORIZED = 401
private const val STATUS_CODE_TOO_MANY_ACCESS = 429

private const val DEFAULT_MAX_RUN_ATTEMPT_COUNT = 2

sealed class TwitterError(message: String? = null) : Exception(message) {
    class Network : TwitterError()
    class Limited : TwitterError()
    object Unauthorized : TwitterError()
    data class UnExpected(val errorCode: Int? = null) : TwitterError()

    companion object {

        operator fun invoke(error: TwitterException): TwitterError {
            return when {
                error.cause is IOException -> Network()
                error is TwitterApiException -> {
                    when (error.statusCode) {
                        STATUS_CODE_UNAUTHORIZED -> Unauthorized
                        STATUS_CODE_TOO_MANY_ACCESS -> Limited()
                        else -> UnExpected(errorCode = error.errorCode)
                    }
                }
                else -> UnExpected()
            }
        }
    }
}

fun ListenableWorker.retryIfNeeded(
    cause: TwitterError,
    maxRunAttemptCount: Int = DEFAULT_MAX_RUN_ATTEMPT_COUNT
): WorkResult {
    return when (cause) {
        is TwitterError.Network -> {
            if (runAttemptCount < maxRunAttemptCount) {
                Timber.d("Retry: $runAttemptCount / $maxRunAttemptCount")
                WorkResult.retry()
            } else {
                Timber.d("Stop retry.")
                WorkResult.failure()
            }
        }
        else -> WorkResult.failure()
            .also { Timber.d("Failed work: $cause") }
    }
}