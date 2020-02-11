package com.droibit.looking2.core.data.source.local.usersettings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.droibit.looking2.core.data.source.local.IntPreferenceKey
import com.droibit.looking2.core.data.source.local.getInt
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@RunWith(AndroidJUnit4::class)
class LocalUserSettingsSourceTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var sharedPrefs: SharedPreferences

    @Mock
    private lateinit var keys: LocalUserSettingsSource.Keys

    private lateinit var localSource: LocalUserSettingsSource

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        sharedPrefs = context.getSharedPreferences("test", Context.MODE_PRIVATE)
        localSource = LocalUserSettingsSource(sharedPrefs, keys)
    }

    @Test
    fun numOfTweets_get() {
        val numOfTweetsKey = IntPreferenceKey("numOfTweets", 0)
        whenever(keys.numOfTweets).doReturn(numOfTweetsKey)

        kotlin.run {
            sharedPrefs.edit { putInt(numOfTweetsKey.key, 1) }
            assertThat(localSource.numOfTweets).isEqualTo(1)
        }

        kotlin.run {
            sharedPrefs.edit { putInt(numOfTweetsKey.key, 2) }
            assertThat(localSource.numOfTweets).isEqualTo(2)
        }
    }

    @Test
    fun numOfTweets_get_defaultValue() {
        val numOfTweetsKey = IntPreferenceKey("numOfTweets", 0)
        whenever(keys.numOfTweets).doReturn(numOfTweetsKey)

        assertThat(localSource.numOfTweets).isEqualTo(0)
    }

    @Test
    fun numOfTweets_set() {
        val numOfTweetsKey = IntPreferenceKey("numOfTweets", 0)
        whenever(keys.numOfTweets).doReturn(numOfTweetsKey)

        kotlin.run {
            localSource.numOfTweets = 1
            assertThat(sharedPrefs.getInt(numOfTweetsKey)).isEqualTo(1)
        }

        kotlin.run {
            localSource.numOfTweets = 2
            assertThat(sharedPrefs.getInt(numOfTweetsKey)).isEqualTo(2)

        }
    }
}