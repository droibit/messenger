package com.droibit.looking2.core.data.repository.usersettings

import com.droibit.looking2.core.data.source.local.usersettings.LocalUserSettingsSource
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class UserSettingsRepositoryTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var localSource: LocalUserSettingsSource

    @InjectMocks
    private lateinit var repository: UserSettingsRepository

    @Test
    fun numOfTweets() {
        whenever(localSource.numOfTweets)
            .thenReturn(15)
            .thenReturn(20)

        assertThat(repository.numOfTweets).isEqualTo(15)
        assertThat(repository.numOfTweets).isEqualTo(20)
    }
}