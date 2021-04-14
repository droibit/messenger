package com.droibit.looking2.core.model.account

import com.google.common.truth.Truth.assertThat
import com.twitter.sdk.android.core.TwitterException
import java.io.IOException
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class AuthenticationErrorTest {

    @Test
    fun invoke_toNetworkError() {
        val error = mock<TwitterException> {
            on { this.cause } doReturn mock<IOException>()
        }
        assertThat(AuthenticationError(error))
            .isEqualTo(AuthenticationError.Network)
    }

    @Test
    fun invoke_toUnExpected() {
        val error = mock<TwitterException>()
        assertThat(AuthenticationError(error))
            .isEqualTo(AuthenticationError.UnExpected)
    }
}
