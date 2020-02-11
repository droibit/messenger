package com.droibit.looking2.core.data.repository.userlist

import com.droibit.looking2.core.TestCoroutinesDispatcherProvider
import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterSource
import com.droibit.looking2.core.data.source.remote.twitter.userlist.RemoteUserListSource
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.UserList
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.isNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.same
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class UserListRepositoryTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var remoteSource: RemoteUserListSource

    @Mock
    private lateinit var localSource: LocalTwitterSource

    @[Spy Suppress("unused")]
    private var dispatcherProvider: CoroutinesDispatcherProvider =
        TestCoroutinesDispatcherProvider()

    @InjectMocks
    private lateinit var repository: UserListRepository

    @Test
    fun getMyLists_success() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localSource.activeSession)
            .thenReturn(session)

        val myLists = mock<List<UserList>>()
        whenever(remoteSource.getUserLists(any(), anyOrNull()))
            .thenReturn(myLists)

        val actualMyLists = repository.getMyLists()
        assertThat(actualMyLists).isEqualTo(myLists)

        verify(remoteSource).getUserLists(same(session), isNull())
    }

    @Test
    fun getMyLists_communicationError() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localSource.activeSession)
            .thenReturn(session)

        val error = mock<TwitterError>()
        whenever(remoteSource.getUserLists(any(), anyOrNull()))
            .thenThrow(error)

        try {
            repository.getMyLists()
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(error)
        }

        verify(remoteSource).getUserLists(same(session), isNull())
    }

    @Test
    fun getMyLists_unauthorizedError() = runBlockingTest {
        whenever(localSource.activeSession)
            .thenReturn(null)

        try {
            repository.getMyLists()
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isEqualTo(TwitterError.Unauthorized)
        }

        verify(remoteSource, never()).getUserLists(any(), anyOrNull())
    }
}