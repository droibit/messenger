package com.droibit.looking2.core.model.account

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.twitter.sdk.android.core.TwitterException
import org.junit.Test
import java.io.IOException

class AuthenticationErrorTest {

    @Test
    fun invoke_toNetworkError() {
        val error = mock<TwitterException> {
            on { cause } doReturn mock<IOException>()
        }
        assertThat(AuthenticationError(error)).isEqualTo(AuthenticationError.Network)
    }

    @Test
    fun invoke_toUnExpected() {
        val error = mock<TwitterException>()
        assertThat(AuthenticationError(error)).isEqualTo(AuthenticationError.UnExpected)
    }
}