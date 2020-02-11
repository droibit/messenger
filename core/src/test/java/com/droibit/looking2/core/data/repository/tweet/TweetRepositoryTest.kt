package com.droibit.looking2.core.data.repository.tweet

import com.droibit.looking2.core.TestCoroutinesDispatcherProvider
import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterSource
import com.droibit.looking2.core.data.source.remote.twitter.tweet.RemoteTweetSource
import com.droibit.looking2.core.model.tweet.TwitterError
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.lang.Exception

class TweetRepositoryTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var remoteSource: RemoteTweetSource

    @Mock
    private lateinit var localSource: LocalTwitterSource

    @[Spy Suppress("unused")]
    private var dispatcherProvider: CoroutinesDispatcherProvider =
        TestCoroutinesDispatcherProvider()

    @InjectMocks
    private lateinit var repository: TweetRepository

    @Test
    fun tweet_success() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localSource.activeSession)
            .thenReturn(session)

        val text = "tweet_text"
        val inReplyToId = Long.MAX_VALUE
        repository.tweet(text, inReplyToId)

        verify(remoteSource).tweet(session, text, inReplyToId)
    }

    @Test
    fun tweet_communicationError() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localSource.activeSession)
            .thenReturn(session)

        val error = mock<TwitterError>()
        whenever(remoteSource.tweet(any(), anyString(), anyOrNull()))
            .thenThrow(error)

        val text = "tweet_text"
        val inReplyToId = Long.MAX_VALUE
        try {
            repository.tweet(text, inReplyToId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(error)
        }

        verify(remoteSource).tweet(session, text, inReplyToId)
    }

    @Test
    fun tweet_unauthorizedError() = runBlockingTest {
        whenever(localSource.activeSession)
            .thenReturn(null)

        try {
            val text = "tweet_text"
            val inReplyToId = Long.MAX_VALUE
            repository.tweet(text, inReplyToId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(TwitterError.Unauthorized)
        }

        verify(remoteSource, never()).tweet(any(), anyString(), anyOrNull())
    }

    @Test
    fun retweet_success() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localSource.activeSession)
            .thenReturn(session)

        val tweetId = Long.MAX_VALUE
        repository.retweet(tweetId)

        verify(remoteSource).retweet(session, tweetId)
    }

    @Test
    fun retweet_communicationError() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localSource.activeSession)
            .thenReturn(session)

        val error = mock<TwitterError>()
        whenever(remoteSource.retweet(any(), anyLong()))
            .thenThrow(error)

        val tweetId = Long.MAX_VALUE
        try {
            repository.retweet(tweetId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(error)
        }

        verify(remoteSource).retweet(session, tweetId)
    }

    @Test
    fun retweet_unauthorizedError() = runBlockingTest {
        whenever(localSource.activeSession)
            .thenReturn(null)

        val tweetId = Long.MAX_VALUE
        try {
            repository.retweet(tweetId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(TwitterError.Unauthorized)
        }

        verify(remoteSource, never()).retweet(any(), anyLong())
    }

    @Test
    fun likeTweet_success() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localSource.activeSession)
            .thenReturn(session)

        val tweetId = Long.MAX_VALUE
        repository.likeTweet(tweetId)

        verify(remoteSource).likeTweet(session, tweetId)
    }

    @Test
    fun likeTweet_communicationError() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localSource.activeSession)
            .thenReturn(session)

        val error = mock<TwitterError>()
        whenever(remoteSource.likeTweet(any(), anyLong()))
            .thenThrow(error)

        val tweetId = Long.MAX_VALUE
        try {
            repository.likeTweet(tweetId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(error)
        }

        verify(remoteSource).likeTweet(session, tweetId)
    }

    @Test
    fun likeTweet_unauthorizedError() = runBlockingTest {
        whenever(localSource.activeSession)
            .thenReturn(null)

        val tweetId = Long.MAX_VALUE
        try {
            repository.likeTweet(tweetId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(TwitterError.Unauthorized)
        }

        verify(remoteSource, never()).likeTweet(any(), anyLong())
    }
}