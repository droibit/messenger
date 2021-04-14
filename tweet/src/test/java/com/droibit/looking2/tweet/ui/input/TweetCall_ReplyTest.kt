package com.droibit.looking2.tweet.ui.input

import android.annotation.SuppressLint
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@Suppress("ClassName")
class TweetCall_ReplyTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var workManager: WorkManager

    private var replyTweetId: Long = Long.MAX_VALUE

    private lateinit var call: TweetCall.Reply

    @Before
    fun setUp() {
        call = TweetCall.Reply(workManager, replyTweetId)
    }

    @SuppressLint("RestrictedApi")
    @Test
    fun buildWorkRequest() {
        val text = "tweet_text"
        val work = call.buildWorkRequest(text)
        assertThat(work).isInstanceOf(OneTimeWorkRequest::class.java)

        val workSpec = work.workSpec
        assertThat(workSpec.input.keyValueMap).containsExactly(
            TweetCall.KEY_TWEET, text,
            TweetCall.KEY_REPLY_TWEET_ID, replyTweetId
        )
        assertThat(workSpec.constraints.requiredNetworkType)
            .isEqualTo(NetworkType.CONNECTED)
    }
}
