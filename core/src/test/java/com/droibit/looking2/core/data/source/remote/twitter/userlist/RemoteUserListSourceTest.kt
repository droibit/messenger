package com.droibit.looking2.core.data.source.remote.twitter.userlist

import com.droibit.looking2.core.data.source.remote.mockErrorCall
import com.droibit.looking2.core.data.source.remote.mockSuccessfulCall
import com.droibit.looking2.core.data.source.remote.twitter.api.AppTwitterApiClient
import com.droibit.looking2.core.data.source.remote.twitter.api.list.UserList as UserListResponse
import com.droibit.looking2.core.data.source.remote.twitter.api.mockAppTwitterApiClient
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.UserList
import com.google.common.truth.Truth.assertThat
import com.ibm.icu.impl.Assert.fail
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import java.io.IOException
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RemoteUserListSourceTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @[Mock Suppress("unused")]
    private lateinit var twitterCore: TwitterCore

    @Mock
    private lateinit var userListMapper: UserListMapper

    @[Spy InjectMocks]
    private lateinit var remoteSource: RemoteUserListSource

    private lateinit var apiClient: AppTwitterApiClient

    @Before
    fun setUp() {
        apiClient = mockAppTwitterApiClient()
        doReturn(apiClient).whenever(remoteSource).get(any())
    }

    @Test
    fun getUserLists_success() = runBlockingTest {
        val userListsResponse = mock<List<UserListResponse>>()
        val call = mockSuccessfulCall(userListsResponse)
        whenever(
            apiClient.userListService.list(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).doReturn(call)

        val userLists = mock<List<UserList>>()
        whenever(userListMapper.toUserLists(any()))
            .thenReturn(userLists)

        val session = mock<TwitterSession>()
        val actualUserLists = remoteSource.getUserLists(session, userId = null)
        assertThat(actualUserLists).isEqualTo(userLists)

        verify(apiClient.userListService).list(
            isNull(),
            isNull(),
            isNull()
        )
        verify(userListMapper).toUserLists(userListsResponse)
    }

    @Test
    fun getUserLists_communicationError() = runBlockingTest {
        val error = mock<IOException>()
        val call = mockErrorCall<List<UserListResponse>>(error)
        whenever(
            apiClient.userListService.list(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).doReturn(call)

        val session = mock<TwitterSession>()

        try {
            remoteSource.getUserLists(session, userId = null)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TwitterError::class.java)
        }

        verify(apiClient.userListService).list(
            isNull(),
            isNull(),
            isNull()
        )
        verify(userListMapper, never()).toUserLists(any())
    }

    @Test
    fun getUserLists_parseError() = runBlockingTest {
        val userListsResponse = mock<List<UserListResponse>>()
        val call = mockSuccessfulCall(userListsResponse)
        whenever(
            apiClient.userListService.list(
                anyOrNull(),
                anyOrNull(),
                anyOrNull()
            )
        ).doReturn(call)

        val error = mock<TwitterException>()
        whenever(userListMapper.toUserLists(any()))
            .thenThrow(error)

        val session = mock<TwitterSession>()
        try {
            remoteSource.getUserLists(session, userId = null)
            fail("error")
        } catch (e: Exception) {
            assertThat(e).isInstanceOf(TwitterError::class.java)
        }

        verify(apiClient.userListService).list(
            isNull(),
            isNull(),
            isNull()
        )
        verify(userListMapper).toUserLists(userListsResponse)
    }
}
