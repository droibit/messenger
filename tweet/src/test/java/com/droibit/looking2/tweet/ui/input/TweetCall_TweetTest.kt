package com.droibit.looking2.tweet.ui.input

import android.annotation.SuppressLint
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@Suppress("ClassName")
class TweetCall_TweetTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @[Mock Suppress("unused")]
    private lateinit var workManager: WorkManager

    @InjectMocks
    private lateinit var call: TweetCall.Tweet

    @SuppressLint("RestrictedApi")
    @Test
    fun buildWorkRequest() {
        val text = "tweet_text"
        val work = call.buildWorkRequest(text)
        assertThat(work).isInstanceOf(OneTimeWorkRequest::class.java)

        val workSpec = work.workSpec
        assertThat(workSpec.input.keyValueMap).containsExactly(
            TweetCall.KEY_TWEET, text
        )
        assertThat(workSpec.constraints.requiredNetworkType)
            .isEqualTo(NetworkType.CONNECTED)
    }
}