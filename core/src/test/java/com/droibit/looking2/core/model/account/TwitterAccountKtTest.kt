package com.droibit.looking2.core.model.account

import com.google.common.truth.Truth.assertThat
import com.twitter.sdk.android.core.TwitterSession
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class TwitterAccountKtTest {

    @Test
    fun toAccount_TwitterSession() {
        val session = mock<TwitterSession> {
            on { this.userId } doReturn 1L
            on { this.userName } doReturn "test"
        }
        assertThat(session.toAccount(active = true)).isEqualTo(
            TwitterAccount(1L, "test", active = true)
        )
        assertThat(session.toAccount(active = false)).isEqualTo(
            TwitterAccount(1L, "test", active = false)
        )
    }
}
