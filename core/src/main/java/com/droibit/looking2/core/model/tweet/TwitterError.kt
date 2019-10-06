package com.droibit.looking2.core.model.tweet

import com.twitter.sdk.android.core.TwitterApiException
import com.twitter.sdk.android.core.TwitterException
import java.io.IOException

private const val STATUS_CODE_UNAUTHORIZED = 401
private const val STATUS_CODE_TOO_MANY_ACCESS = 429

// TODO: Consider adding auth error & limited api error.
sealed class TwitterError(message: String? = null) : Exception(message) {
    class Network : TwitterError()
    class Limited: TwitterError()
    class Unauthorized : TwitterError()
    class UnExpected : TwitterError()
}

fun TwitterException.toTwitterError(): TwitterError {
    if (this.cause is IOException) return TwitterError.Network()
    if (this !is TwitterApiException) return TwitterError.UnExpected()

    return when (this.statusCode) {
        STATUS_CODE_UNAUTHORIZED -> TwitterError.Unauthorized()
        STATUS_CODE_TOO_MANY_ACCESS -> TwitterError.Limited()
        else -> TwitterError.UnExpected()
    }
}