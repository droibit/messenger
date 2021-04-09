package com.droibit.looking2.core.data.source.remote.twitter.api

import com.google.common.truth.Truth.assertThat
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class AppTwitterApiClientFactoryDelegateTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var twitterCore: TwitterCore

    @InjectMocks
    private lateinit var factory: AppTwitterApiClientFactoryDelegate

    @Test
    fun get() {
        val client = mock<AppTwitterApiClient>()
        whenever(twitterCore.getApiClient(any())).thenReturn(client)

        val session = mock<TwitterSession>()
        assertThat(factory.get(session)).isSameInstanceAs(client)
    }
}