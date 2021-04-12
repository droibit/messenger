package com.droibit.looking2.core.data.repository.timeline

import com.droibit.looking2.core.TestCoroutinesDispatcherProvider
import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterSource
import com.droibit.looking2.core.data.source.local.usersettings.LocalUserSettingsSource
import com.droibit.looking2.core.data.source.remote.twitter.timeline.RemoteTimelineSource
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import com.google.common.truth.Truth.assertThat
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.eq
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.same
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class TimelineRepositoryTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var remoteTimelineSource: RemoteTimelineSource

    @Mock
    private lateinit var localTwitterSource: LocalTwitterSource

    @Mock
    private lateinit var localUserSettingsSource: LocalUserSettingsSource

    @[Spy Suppress("unused")]
    private var dispatcherProvider: CoroutinesDispatcherProvider =
        TestCoroutinesDispatcherProvider()

    @InjectMocks
    private lateinit var repository: TimelineRepository

    @Test
    fun getHomeTimeline_success() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localTwitterSource.activeSession)
            .thenReturn(session)

        val numOfTweets = Int.MAX_VALUE
        whenever(localUserSettingsSource.numOfTweets)
            .thenReturn(numOfTweets)

        val timeline = mock<List<Tweet>>()
        whenever(remoteTimelineSource.getHomeTimeline(any(), anyInt(), anyOrNull()))
            .thenReturn(timeline)

        val actualTimeline = repository.getHomeTimeline(sinceId = null)
        assertThat(actualTimeline).isEqualTo(actualTimeline)

        verify(remoteTimelineSource).getHomeTimeline(same(session), eq(numOfTweets), isNull())
    }

    @Test
    fun getHomeTimeline_communicationError() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localTwitterSource.activeSession)
            .thenReturn(session)

        val numOfTweets = Int.MAX_VALUE
        whenever(localUserSettingsSource.numOfTweets)
            .thenReturn(numOfTweets)

        val error = mock<TwitterError>()
        whenever(remoteTimelineSource.getHomeTimeline(any(), anyInt(), anyOrNull()))
            .thenThrow(error)

        try {
            repository.getHomeTimeline(sinceId = null)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(error)
        }

        verify(remoteTimelineSource).getHomeTimeline(same(session), eq(numOfTweets), isNull())
    }

    @Test
    fun getHomeTimeline_unauthorizedError() = runBlockingTest {
        whenever(localTwitterSource.activeSession)
            .thenReturn(null)

        try {
            repository.getHomeTimeline(sinceId = null)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(TwitterError.Unauthorized)
        }

        verify(localUserSettingsSource, never()).numOfTweets
        verify(remoteTimelineSource, never()).getHomeTimeline(any(), anyInt(), anyOrNull())
    }

    @Test
    fun getMentionsTimeline_success() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localTwitterSource.activeSession)
            .thenReturn(session)

        val numOfTweets = Int.MAX_VALUE
        whenever(localUserSettingsSource.numOfTweets)
            .thenReturn(numOfTweets)

        val timeline = mock<List<Tweet>>()
        whenever(remoteTimelineSource.getMentionsTimeline(any(), anyInt(), anyOrNull()))
            .thenReturn(timeline)

        val actualTimeline = repository.getMentionsTimeline(sinceId = null)
        assertThat(actualTimeline).isEqualTo(actualTimeline)

        verify(remoteTimelineSource).getMentionsTimeline(same(session), eq(numOfTweets), isNull())
    }

    @Test
    fun getMentionsTimeline_communicationError() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localTwitterSource.activeSession)
            .thenReturn(session)

        val numOfTweets = Int.MAX_VALUE
        whenever(localUserSettingsSource.numOfTweets)
            .thenReturn(numOfTweets)

        val error = mock<TwitterError>()
        whenever(remoteTimelineSource.getMentionsTimeline(any(), anyInt(), anyOrNull()))
            .thenThrow(error)

        try {
            repository.getMentionsTimeline(sinceId = null)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(error)
        }

        verify(remoteTimelineSource).getMentionsTimeline(same(session), eq(numOfTweets), isNull())
    }

    @Test
    fun getMentionsTimeline_unauthorizedError() = runBlockingTest {
        whenever(localTwitterSource.activeSession)
            .thenReturn(null)

        try {
            repository.getMentionsTimeline(sinceId = null)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(TwitterError.Unauthorized)
        }

        verify(localUserSettingsSource, never()).numOfTweets
        verify(remoteTimelineSource, never()).getMentionsTimeline(any(), anyInt(), anyOrNull())
    }

    @Test
    fun getUserListTimeline_success() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localTwitterSource.activeSession)
            .thenReturn(session)

        val numOfTweets = Int.MAX_VALUE
        whenever(localUserSettingsSource.numOfTweets)
            .thenReturn(numOfTweets)

        val timeline = mock<List<Tweet>>()
        whenever(remoteTimelineSource.getUserListTimeline(any(), anyLong(), anyInt(), anyOrNull()))
            .thenReturn(timeline)

        val listId = Long.MAX_VALUE
        val actualTimeline = repository.getUserListTimeline(listId, sinceId = null)
        assertThat(actualTimeline).isEqualTo(actualTimeline)

        verify(remoteTimelineSource).getUserListTimeline(
            same(session),
            eq(listId),
            eq(numOfTweets),
            isNull()
        )
    }

    @Test
    fun getUserListTimeline_communicationError() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localTwitterSource.activeSession)
            .thenReturn(session)

        val numOfTweets = Int.MAX_VALUE
        whenever(localUserSettingsSource.numOfTweets)
            .thenReturn(numOfTweets)

        val error = mock<TwitterError>()
        whenever(remoteTimelineSource.getUserListTimeline(any(), anyLong(), anyInt(), anyOrNull()))
            .thenThrow(error)

        val listId = Long.MAX_VALUE
        try {
            repository.getUserListTimeline(listId, sinceId = null)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(error)
        }

        verify(remoteTimelineSource).getUserListTimeline(
            same(session),
            eq(listId),
            eq(numOfTweets),
            isNull()
        )
    }

    @Test
    fun getUserListTimeline_unauthorizedError() = runBlockingTest {
        whenever(localTwitterSource.activeSession)
            .thenReturn(null)

        try {
            val listId = Long.MAX_VALUE
            repository.getUserListTimeline(listId, sinceId = null)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(TwitterError.Unauthorized)
        }

        verify(remoteTimelineSource, never()).getUserListTimeline(
            any(),
            anyLong(),
            anyInt(),
            anyOrNull()
        )
    }
}
