package com.droibit.looking2.core.model.account

import com.twitter.sdk.android.core.TwitterException
import java.io.IOException

sealed class AuthenticationError(message: String? = null) : Exception(message) {
    object Network : AuthenticationError()
    data class PlayServices(val statusCode: Int) : AuthenticationError()
    object UnExpected : AuthenticationError()

    companion object {

        operator fun invoke(error: TwitterException): AuthenticationError {
            return if (error.cause is IOException) Network else UnExpected
        }
    }
}