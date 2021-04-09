package com.droibit.looking2.core.data.source.remote.twitter.tweet

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.droibit.looking2.core.data.source.remote.mockErrorCall
import com.droibit.looking2.core.data.source.remote.mockSuccessfulCall
import com.droibit.looking2.core.data.source.remote.twitter.api.AppTwitterApiClient
import com.droibit.looking2.core.data.source.remote.twitter.api.mockAppTwitterApiClient
import com.droibit.looking2.core.model.tweet.TwitterError
import com.google.common.truth.Truth.assertThat
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.io.IOException
import com.twitter.sdk.android.core.models.Tweet as TweetResponse

@RunWith(AndroidJUnit4::class)
class RemoteTweetSourceTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @[Mock Suppress("unused")]
    private lateinit var twitterCore: TwitterCore

    @[Spy InjectMocks]
    private lateinit var remoteSource: RemoteTweetSource

    private lateinit var apiClient: AppTwitterApiClient

    @Before
    fun setUp() {
        apiClient = mockAppTwitterApiClient()
        doReturn(apiClient).whenever(remoteSource).get(any())
    }

    @Test
    fun tweet_success() = runBlockingTest {
        val tweetResponse = mock<TweetResponse>()
        val call = mockSuccessfulCall(tweetResponse)
        whenever(
            apiClient.statusesService.update(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val session = mock<TwitterSession>()
        val text = "tweet_text"
        val inReplyToId = Long.MAX_VALUE
        remoteSource.tweet(session, text, inReplyToId)

        verify(apiClient.statusesService).update(
            eq(text),
            eq(inReplyToId),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
    }

    @Test
    fun tweet_communicationError() = runBlockingTest {
        val error = mock<IOException>()
        val call = mockErrorCall<TweetResponse>(error)
        whenever(
            apiClient.statusesService.update(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val session = mock<TwitterSession>()
        val text = "tweet_text"
        val inReplyToId = Long.MAX_VALUE
        try {
            remoteSource.tweet(session, text, inReplyToId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TwitterError::class.java)
        }

        verify(apiClient.statusesService).update(
            eq(text),
            eq(inReplyToId),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
    }

    @Test
    fun retweet_success() = runBlockingTest {
        val tweetResponse = mock<TweetResponse>()
        val call = mockSuccessfulCall(tweetResponse)
        whenever(
            apiClient.statusesService.retweet(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val session = mock<TwitterSession>()
        val tweetId = Long.MAX_VALUE
        remoteSource.retweet(session, tweetId)

        verify(apiClient.statusesService).retweet(eq(tweetId), isNull())
    }

    @Test
    fun retweet_communicationError() = runBlockingTest {
        val error = mock<IOException>()
        val call = mockErrorCall<TweetResponse>(error)
        whenever(
            apiClient.statusesService.retweet(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val session = mock<TwitterSession>()
        val tweetId = Long.MAX_VALUE

        try {
            remoteSource.retweet(session, tweetId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TwitterError::class.java)
        }

        verify(apiClient.statusesService).retweet(eq(tweetId), isNull())
    }

    @Test
    fun retweet_alreadyRetweet() = runBlockingTest {
        val errorBody = """{"errors":[{"code":327,"message":"error"}]}"""
        val call = mockErrorCall<TweetResponse>(
            statusCode = 403,
            errorBody = errorBody.toResponseBody()
        )
        whenever(
            apiClient.statusesService.retweet(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val session = mock<TwitterSession>()
        val tweetId = Long.MAX_VALUE
        remoteSource.retweet(session, tweetId)

        verify(apiClient.statusesService).retweet(eq(tweetId), isNull())
    }

    @Test
    fun likeTweet_success() = runBlockingTest {
        val tweetResponse = mock<TweetResponse>()
        val call = mockSuccessfulCall(tweetResponse)
        whenever(
            apiClient.favoriteService.create(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val session = mock<TwitterSession>()
        val tweetId = Long.MAX_VALUE
        remoteSource.likeTweet(session, tweetId)

        verify(apiClient.favoriteService).create(eq(tweetId), isNull())
    }

    @Test
    fun likeTweet_communicationError() = runBlockingTest {
        val error = mock<IOException>()
        val call = mockErrorCall<TweetResponse>(error)
        whenever(
            apiClient.favoriteService.create(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val session = mock<TwitterSession>()
        val tweetId = Long.MAX_VALUE

        try {
            remoteSource.likeTweet(session, tweetId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TwitterError::class.java)
        }

        verify(apiClient.favoriteService).create(eq(tweetId), isNull())
    }

    @Test
    fun likeTweet_alreadyFavorite() = runBlockingTest {
        val errorBody = """{"errors":[{"code":139,"message":"error"}]}"""
        val call = mockErrorCall<TweetResponse>(
            statusCode = 403,
            errorBody = errorBody.toResponseBody()
        )
        whenever(
            apiClient.favoriteService.create(
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val session = mock<TwitterSession>()
        val tweetId = Long.MAX_VALUE
        remoteSource.likeTweet(session, tweetId)

        verify(apiClient.favoriteService).create(eq(tweetId), isNull())
    }
}