package com.droibit.looking2.core.model.tweet

import com.google.common.truth.Truth.assertThat
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import com.twitter.sdk.android.core.TwitterApiException
import com.twitter.sdk.android.core.TwitterApiException.DEFAULT_ERROR_CODE
import com.twitter.sdk.android.core.TwitterException
import org.junit.Test
import java.io.IOException

private const val UNAUTHORIZED_ACCESS = 401
private const val TOO_MANY_REQUESTS = 429

class TwitterErrorTest {

    @Test
    fun invoke_toNetworkError() {
        val error = mock<TwitterException> {
            on { cause } doReturn mock<IOException>()
        }
        assertThat(TwitterError(error))
            .isEqualTo(TwitterError.Network)
    }

    @Test
    fun invoke_toUnauthorizedError() {
        val error = mock<TwitterApiException> {
            on { statusCode } doReturn UNAUTHORIZED_ACCESS
        }
        assertThat(TwitterError(error))
            .isEqualTo(TwitterError.Unauthorized)
    }

    @Test
    fun invoke_toLimitedError() {
        val error = mock<TwitterApiException> {
            on { statusCode } doReturn TOO_MANY_REQUESTS
        }
        assertThat(TwitterError(error))
            .isEqualTo(TwitterError.Limited)
    }

    @Test
    fun invoke_toUnExpectedError() {
        kotlin.run {
            val error = mock<TwitterApiException> {
                on { statusCode } doReturn 403
                on { errorCode } doReturn 327
            }
            assertThat(TwitterError(error))
                .isEqualTo(TwitterError.UnExpected(327))
        }

        kotlin.run {
            class UnknownError : TwitterException("error")
            val error = UnknownError()
            assertThat(TwitterError(error))
                .isEqualTo(TwitterError.UnExpected(DEFAULT_ERROR_CODE, "error"))
        }
    }
}