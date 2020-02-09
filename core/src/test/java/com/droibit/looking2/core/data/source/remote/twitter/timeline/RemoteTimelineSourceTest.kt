package com.droibit.looking2.core.data.source.remote.twitter.timeline

import com.droibit.looking2.core.data.source.remote.mockErrorCall
import com.droibit.looking2.core.data.source.remote.mockSuccessfulCall
import com.droibit.looking2.core.data.source.remote.twitter.api.AppTwitterApiClient
import com.droibit.looking2.core.data.source.remote.twitter.api.mockAppTwitterApiClient
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.isNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.io.IOException
import com.twitter.sdk.android.core.models.Tweet as TweetResponse

class RemoteTimelineSourceTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Suppress("unused")
    @Mock
    private lateinit var twitterCore: TwitterCore

    @Mock
    private lateinit var timelineMapper: TimelineMapper

    @Spy
    @InjectMocks
    private lateinit var remoteSource: RemoteTimelineSource

    private lateinit var apiClient: AppTwitterApiClient

    @Before
    fun setUp() {
        apiClient = mockAppTwitterApiClient()
        doReturn(apiClient).whenever(remoteSource).get(any())
    }

    @Test
    fun getHomeTimeline_success() = runBlockingTest {
        val timelineResponse = mock<List<TweetResponse>>()
        val call = mockSuccessfulCall(timelineResponse)
        whenever(
            apiClient.statusesService.homeTimeline(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val timeline = mock<List<Tweet>>()
        whenever(timelineMapper.toTimeline(any())).thenReturn(timeline)

        val session = mock<TwitterSession>()
        val count = 1
        val sinceId: Long? = null
        val actualTimeline = remoteSource.getHomeTimeline(session, count, sinceId)
        assertThat(actualTimeline).isEqualTo(timeline)

        verify(remoteSource).get(session)
        verify(apiClient.statusesService).homeTimeline(
            eq(count),
            eq(sinceId),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
        verify(timelineMapper).toTimeline(timelineResponse)
    }

    @Test
    fun getHomeTimeline_communicationError() = runBlockingTest {
        val error = mock<IOException>()
        val call = mockErrorCall<List<TweetResponse>>(error)
        whenever(
            apiClient.statusesService.homeTimeline(
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
        val count = 1
        val sinceId: Long? = null

        try {
            remoteSource.getHomeTimeline(session, count, sinceId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TwitterError::class.java)
        }

        verify(remoteSource).get(session)
        verify(apiClient.statusesService).homeTimeline(
            eq(count),
            eq(sinceId),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
        verify(timelineMapper, never()).toTimeline(any())
    }

    @Test
    fun getHomeTimeline_parseError() = runBlockingTest {
        val timelineResponse = mock<List<TweetResponse>>()
        val call = mockSuccessfulCall(timelineResponse)
        whenever(
            apiClient.statusesService.homeTimeline(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val error = mock<TwitterException>()
        whenever(timelineMapper.toTimeline(any())).thenThrow(error)

        val session = mock<TwitterSession>()
        val count = 1
        val sinceId: Long? = null

        try {
            remoteSource.getHomeTimeline(session, count, sinceId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TwitterError::class.java)
        }

        verify(remoteSource).get(session)
        verify(apiClient.statusesService).homeTimeline(
            eq(count),
            eq(sinceId),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
        verify(timelineMapper).toTimeline(timelineResponse)
    }

    @Test
    fun getMentionsTimeline_success() = runBlockingTest {
        val timelineResponse = mock<List<TweetResponse>>()
        val call = mockSuccessfulCall(timelineResponse)
        whenever(
            apiClient.statusesService.mentionsTimeline(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val timeline = mock<List<Tweet>>()
        whenever(timelineMapper.toTimeline(any())).thenReturn(timeline)

        val session = mock<TwitterSession>()
        val count = 1
        val sinceId: Long? = null
        val actualTimeline = remoteSource.getMentionsTimeline(session, count, sinceId)
        assertThat(actualTimeline).isEqualTo(timeline)

        verify(remoteSource).get(session)
        verify(apiClient.statusesService).mentionsTimeline(
            eq(count),
            eq(sinceId),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
        verify(timelineMapper).toTimeline(timelineResponse)
    }

    @Test
    fun getMentionsTimeline_communicationError() = runBlockingTest {
        val error = mock<IOException>()
        val call = mockErrorCall<List<TweetResponse>>(error)
        whenever(
            apiClient.statusesService.mentionsTimeline(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val session = mock<TwitterSession>()
        val count = 1
        val sinceId: Long? = null

        try {
            remoteSource.getMentionsTimeline(session, count, sinceId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TwitterError::class.java)
        }

        verify(remoteSource).get(session)
        verify(apiClient.statusesService).mentionsTimeline(
            eq(count),
            eq(sinceId),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
        verify(timelineMapper, never()).toTimeline(any())
    }

    @Test
    fun getMentionsTimeline_parseError() = runBlockingTest {
        val timelineResponse = mock<List<TweetResponse>>()
        val call = mockSuccessfulCall(timelineResponse)
        whenever(
            apiClient.statusesService.mentionsTimeline(
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(call)

        val error = mock<TwitterException>()
        whenever(timelineMapper.toTimeline(any())).thenThrow(error)

        val session = mock<TwitterSession>()
        val count = 1
        val sinceId: Long? = null

        try {
            remoteSource.getMentionsTimeline(session, count, sinceId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TwitterError::class.java)
        }

        verify(remoteSource).get(session)
        verify(apiClient.statusesService).mentionsTimeline(
            eq(count),
            eq(sinceId),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
        verify(timelineMapper).toTimeline(timelineResponse)
    }

    @Test
    fun getUserListTimeline_success() = runBlockingTest {
        val timelineResponse = mock<List<TweetResponse>>()
        val call = mockSuccessfulCall(timelineResponse)
        whenever(
            apiClient.userListService.statuses(
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

        val timeline = mock<List<Tweet>>()
        whenever(timelineMapper.toTimeline(any())).thenReturn(timeline)

        val session = mock<TwitterSession>()
        val listId = 2L
        val count = 1
        val sinceId: Long? = null
        val actualTimeline = remoteSource.getUserListTimeline(session, listId, count, sinceId)
        assertThat(actualTimeline).isEqualTo(timeline)

        verify(remoteSource).get(session)
        verify(apiClient.userListService).statuses(
            eq(listId),
            eq(count),
            eq(sinceId),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
        verify(timelineMapper).toTimeline(timelineResponse)
    }

    @Test
    fun getUserListTimeline_communicationError() = runBlockingTest {
        val error = mock<IOException>()
        val call = mockErrorCall<List<TweetResponse>>(error)
        whenever(
            apiClient.userListService.statuses(
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
        val listId = 2L
        val count = 1
        val sinceId: Long? = null

        try {
            remoteSource.getUserListTimeline(session, listId, count, sinceId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TwitterError::class.java)
        }

        verify(remoteSource).get(session)
        verify(apiClient.userListService).statuses(
            eq(listId),
            eq(count),
            eq(sinceId),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
        verify(timelineMapper, never()).toTimeline(any())
    }

    @Test
    fun getUserListTimeline_parseError() = runBlockingTest {
        val timelineResponse = mock<List<TweetResponse>>()
        val call = mockSuccessfulCall(timelineResponse)
        whenever(
            apiClient.userListService.statuses(
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

        val error = mock<TwitterException>()
        whenever(timelineMapper.toTimeline(any())).thenThrow(error)

        val session = mock<TwitterSession>()
        val listId = 2L
        val count = 1
        val sinceId: Long? = null

        try {
            remoteSource.getUserListTimeline(session, listId, count, sinceId)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TwitterError::class.java)
        }

        verify(remoteSource).get(session)
        verify(apiClient.userListService).statuses(
            eq(listId),
            eq(count),
            eq(sinceId),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull(),
            isNull()
        )
        verify(timelineMapper).toTimeline(timelineResponse)
    }
}