package com.droibit.looking2.tweet.ui.input

import androidx.work.WorkManager
import com.droibit.looking2.ui.Activities.Tweet.ReplyTweet
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class TweetCallTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var workManager: WorkManager

    @Test
    fun invoke_TweetCall() {
        val call = TweetCall(workManager, replyTweet = null)
        assertThat(call).isInstanceOf(TweetCall.Tweet::class.java)
    }

    @Ignore("ReplyTweet not found error on CI.")
    @Test
    fun invoke_ReplayCall() {
        val tweetId = Long.MAX_VALUE
        val replyTweet = mock<ReplyTweet> {
            on { this.id } doReturn tweetId
        }
        val call = TweetCall(workManager, replyTweet)
        assertThat(call).isInstanceOf(TweetCall.Reply::class.java)

        val replyCall = call as TweetCall.Reply
        assertThat(replyCall.replyTweetId).isEqualTo(tweetId)
    }
}