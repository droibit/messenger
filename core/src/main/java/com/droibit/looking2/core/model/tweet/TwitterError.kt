package com.droibit.looking2.core.model.tweet

import androidx.work.ListenableWorker
import androidx.work.ListenableWorker.Result as WorkResult
import com.twitter.sdk.android.core.TwitterApiException
import com.twitter.sdk.android.core.TwitterApiException.DEFAULT_ERROR_CODE
import com.twitter.sdk.android.core.TwitterException
import java.io.IOException
import timber.log.Timber

// ref. https://developer.twitter.com/en/docs/ads/general/guides/response-codes
private const val STATUS_CODE_UNAUTHORIZED = 401
private const val STATUS_CODE_TOO_MANY_ACCESS = 429

private const val DEFAULT_MAX_RUN_ATTEMPT_COUNT = 2

sealed class TwitterError(message: String? = null) : Exception(message) {
    object Network : TwitterError()
    object Limited : TwitterError()
    object Unauthorized : TwitterError()
    data class UnExpected(
        val errorCode: Int? = null,
        override val message: String? = null
    ) : TwitterError(message)

    companion object {

        operator fun invoke(error: TwitterException): TwitterError {
            return when {
                error.cause is IOException -> Network
                error is TwitterApiException -> {
                    when (error.statusCode) {
                        STATUS_CODE_UNAUTHORIZED -> Unauthorized
                        STATUS_CODE_TOO_MANY_ACCESS -> Limited
                        else -> UnExpected(error.errorCode, error.errorMessage)
                    }
                }
                else -> UnExpected(DEFAULT_ERROR_CODE, error.message)
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
            val attemptCount = this.runAttemptCount
            if (attemptCount < maxRunAttemptCount) {
                Timber.d("Retry: $attemptCount / $maxRunAttemptCount")
                WorkResult.retry()
            } else {
                Timber.d("Stop retry.")
                WorkResult.failure()
            }
        }
        else ->
            WorkResult.failure()
                .also { Timber.d("Failed work: $cause") }
    }
}
