package com.droibit.looking2.core.data.repository.account

import com.droibit.looking2.core.TestCoroutinesDispatcherProvider
import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterSource
import com.droibit.looking2.core.data.source.remote.twitter.account.RemoteTwitterAccountSource
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class AccountRepositoryTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var remoteSource: RemoteTwitterAccountSource

    @Mock
    private lateinit var localSource: LocalTwitterSource

    @[Spy Suppress("unused")]
    private var dispatcherProvider: CoroutinesDispatcherProvider =
        TestCoroutinesDispatcherProvider()

    @Spy
    private var twitterAccountsSink = MutableStateFlow<List<TwitterAccount>>(emptyList())

    @Mock
    private lateinit var analytics: AnalyticsHelper

    @Spy
    @InjectMocks
    private lateinit var repository: AccountRepository

    @Test
    fun initialize() = runBlockingTest {
        val session = mock<TwitterSession>()
        whenever(localSource.sessions).thenReturn(listOf(session))

        doNothing().whenever(repository).dispatchTwitterAccountsUpdated()

        repository.initialize()

        verify(remoteSource).ensureApiClient(session)
        verify(repository).dispatchTwitterAccountsUpdated()
    }

    // ref. https://www.thuytrinh.dev/test-receiving-events-hot-flow-coroutines/
    @Test
    fun twitterAccounts() = runBlockingTest {
        val recordedValues = mutableListOf<List<TwitterAccount>>()
        val job = launch {
            repository.twitterAccounts()
                .drop(1)    // drop initial value
                .collect { recordedValues.add(it) }
        }

        val account1 = mock<List<TwitterAccount>>()
        twitterAccountsSink.value = account1
        assertThat(recordedValues).containsExactly(account1)

        val account2 = mock<List<TwitterAccount>>()
        twitterAccountsSink.value = account2
        assertThat(recordedValues).containsExactly(account1, account2)

        job.cancel()
    }

    @Test
    fun updateActiveTwitterAccount() = runBlockingTest {
        val oldActiveSession = mock<TwitterSession> {
            on { this.userId } doReturn 1L
            on { this.userName } doReturn "test1"
        }
        val newActiveSession = mock<TwitterSession> {
            on { this.userId } doReturn 2L
            on { this.userName } doReturn "test2"
        }

        whenever(localSource.activeSession)
            .thenReturn(oldActiveSession)
            .thenReturn(newActiveSession)
        whenever(localSource.sessions)
            .thenReturn(listOf(oldActiveSession, newActiveSession))

        whenever(localSource.getSessionBy(anyLong()))
            .thenReturn(newActiveSession)

        val updateAccountId = 2L
        repository.updateActiveTwitterAccount(updateAccountId)

        verify(localSource).setActiveSession(newActiveSession)
        verify(repository).dispatchTwitterAccountsUpdated()
    }

    @Test
    fun updateActiveTwitterAccount_skipUpdate() = runBlockingTest {
        val activeSession = mock<TwitterSession>()
        whenever(localSource.activeSession)
            .thenReturn(activeSession)

        whenever(localSource.getSessionBy(anyLong()))
            .thenReturn(activeSession)

        val updateAccountId = 2L
        repository.updateActiveTwitterAccount(updateAccountId)

        verify(localSource, never()).setActiveSession(any())
        verify(repository, never()).dispatchTwitterAccountsUpdated()
    }

    @Test
    fun signInTwitter_success() = runBlockingTest {
        val requestToken = mock<TwitterAuthToken>()
        whenever(remoteSource.requestTempToken())
            .thenReturn(requestToken)

        val responseUrl = "url"
        whenever(remoteSource.sendAuthorizationRequest(any()))
            .thenReturn(responseUrl)

        val newSession = mock<TwitterSession>()
        whenever(remoteSource.createNewSession(any(), anyString()))
            .thenReturn(newSession)

        val sessions = mock<List<TwitterSession>> {
            on { this.size } doReturn 1
        }
        whenever(localSource.sessions).thenReturn(sessions)

        doNothing().whenever(repository).dispatchTwitterAccountsUpdated()

        val recordedValues = mutableListOf<AuthenticationResult>()
        val job = launch {
            repository.signInTwitter()
                .collect { recordedValues.add(it) }
        }

        assertThat(recordedValues).containsExactly(
            AuthenticationResult.WillAuthenticateOnPhone,
            AuthenticationResult.Success
        )

        val inOrder = inOrder(remoteSource, localSource, analytics, repository)
        inOrder.verify(remoteSource).requestTempToken()
        inOrder.verify(remoteSource).sendAuthorizationRequest(requestToken)
        inOrder.verify(remoteSource).createNewSession(requestToken, responseUrl)
        inOrder.verify(localSource).add(newSession)
        inOrder.verify(remoteSource).ensureApiClient(newSession)
        inOrder.verify(analytics).setNumOfGetTweets(1)
        inOrder.verify(repository).dispatchTwitterAccountsUpdated()

        job.cancel()
    }

    @Test
    fun signInTwitter_error_onRequestTempToken() = runBlockingTest {
        val error = mock<AuthenticationError>()
        whenever(remoteSource.requestTempToken())
            .thenThrow(error)

        val recordedValues = mutableListOf<AuthenticationResult>()
        val job = launch {
            repository.signInTwitter()
                .collect { recordedValues.add(it) }
        }

        assertThat(recordedValues).containsExactly(
            AuthenticationResult.Failure(error)
        )

        verify(remoteSource).requestTempToken()
        verify(remoteSource, never()).sendAuthorizationRequest(any())
        verify(remoteSource, never()).createNewSession(any(), anyString())
        verify(localSource, never()).add(any())
        verify(remoteSource, never()).ensureApiClient(any())
        verify(analytics, never()).setNumOfGetTweets(anyInt())
        verify(repository, never()).dispatchTwitterAccountsUpdated()

        job.cancel()
    }

    @Test
    fun signInTwitter_error_onSendAuthorizationRequest() = runBlockingTest {
        val requestToken = mock<TwitterAuthToken>()
        whenever(remoteSource.requestTempToken())
            .thenReturn(requestToken)

        val error = mock<AuthenticationError>()
        whenever(remoteSource.sendAuthorizationRequest(any()))
            .thenThrow(error)

        val recordedValues = mutableListOf<AuthenticationResult>()
        val job = launch {
            repository.signInTwitter()
                .collect { recordedValues.add(it) }
        }

        assertThat(recordedValues).containsExactly(
            AuthenticationResult.WillAuthenticateOnPhone,
            AuthenticationResult.Failure(error)
        )

        verify(remoteSource).requestTempToken()
        verify(remoteSource).sendAuthorizationRequest(requestToken)
        verify(remoteSource, never()).createNewSession(any(), anyString())
        verify(localSource, never()).add(any())
        verify(remoteSource, never()).ensureApiClient(any())
        verify(analytics, never()).setNumOfGetTweets(anyInt())
        verify(repository, never()).dispatchTwitterAccountsUpdated()

        job.cancel()
    }

    @Test
    fun signInTwitter_error_onCreateNewSession() = runBlockingTest {
        val requestToken = mock<TwitterAuthToken>()
        whenever(remoteSource.requestTempToken())
            .thenReturn(requestToken)

        val responseUrl = "url"
        whenever(remoteSource.sendAuthorizationRequest(any()))
            .thenReturn(responseUrl)

        val error = mock<AuthenticationError>()
        whenever(remoteSource.createNewSession(any(), anyString()))
            .thenThrow(error)
        val recordedValues = mutableListOf<AuthenticationResult>()
        val job = launch {
            repository.signInTwitter()
                .collect { recordedValues.add(it) }
        }

        assertThat(recordedValues).containsExactly(
            AuthenticationResult.WillAuthenticateOnPhone,
            AuthenticationResult.Failure(error)
        )

        verify(remoteSource).requestTempToken()
        verify(remoteSource).sendAuthorizationRequest(requestToken)
        verify(remoteSource).createNewSession(requestToken, responseUrl)
        verify(localSource, never()).add(any())
        verify(remoteSource, never()).ensureApiClient(any())
        verify(analytics, never()).setNumOfGetTweets(anyInt())
        verify(repository, never()).dispatchTwitterAccountsUpdated()

        job.cancel()
    }

    @Test
    fun signOutTwitter() = runBlockingTest {
        val session = mock<TwitterSession>()
        val sessions = listOf(session)
        whenever(localSource.sessions)
            .thenReturn(sessions)

        whenever(localSource.activeSession)
            .thenReturn(null)

        doNothing().whenever(repository).dispatchTwitterAccountsUpdated()

        val accountId = 1L
        repository.signOutTwitter(accountId)

        val inOrder = inOrder(localSource, analytics, repository)
        inOrder.verify(localSource).remove(accountId)
        inOrder.verify(analytics).setNumOfGetTweets(sessions.size)
        inOrder.verify(localSource).setActiveSession(session)
        inOrder.verify(repository).dispatchTwitterAccountsUpdated()
    }

    @Test
    fun signOutTwitter_skipSwitchingActiveSession_emptyAccounts() = runBlockingTest {
        val sessions = emptyList<TwitterSession>()
        whenever(localSource.sessions)
            .thenReturn(sessions)

        whenever(localSource.activeSession)
            .thenReturn(null)

        val accountId = 1L
        repository.signOutTwitter(accountId)

        val inOrder = inOrder(localSource, analytics, repository)
        inOrder.verify(localSource).remove(accountId)
        inOrder.verify(analytics).setNumOfGetTweets(sessions.size)
        inOrder.verify(repository).dispatchTwitterAccountsUpdated()

        verify(localSource, never()).setActiveSession(any())
    }

    @Test
    fun signOutTwitter_skipSwitchingActiveSession_existOtherActiveSession() = runBlockingTest {
        val activeSession = mock<TwitterSession>()
        val sessions = listOf(activeSession)
        whenever(localSource.sessions)
            .thenReturn(sessions)

        whenever(localSource.activeSession)
            .thenReturn(activeSession)

        doNothing().whenever(repository).dispatchTwitterAccountsUpdated()

        val accountId = 1L
        repository.signOutTwitter(accountId)

        val inOrder = inOrder(localSource, analytics, repository)
        inOrder.verify(localSource).remove(accountId)
        inOrder.verify(analytics).setNumOfGetTweets(sessions.size)
        inOrder.verify(repository).dispatchTwitterAccountsUpdated()

        verify(localSource, never()).setActiveSession(any())
    }

    @Test
    fun dispatchTwitterAccounts() = runBlockingTest {
        val session1 = mock<TwitterSession> {
            on { this.userId } doReturn 1L
            on { this.userName } doReturn "test1"
        }
        val session2 = mock<TwitterSession> {
            on { this.userId } doReturn 2L
            on { this.userName } doReturn "test2"
        }
        whenever(localSource.activeSession).thenReturn(session1)
        whenever(localSource.sessions).thenReturn(listOf(session1, session2))

        repository.dispatchTwitterAccountsUpdated()

        val account1 = TwitterAccount(
            session1.userId,
            session1.userName,
            active = true
        )
        val account2 = TwitterAccount(
            session2.userId,
            session2.userName,
            active = false
        )
        verify(twitterAccountsSink).value = listOf(account1, account2)
    }

    @Test
    fun dispatchTwitterAccounts_noSessions() {
        whenever(localSource.activeSession).thenReturn(null)
        whenever(localSource.sessions).thenReturn(emptyList())

        repository.dispatchTwitterAccountsUpdated()
        verify(twitterAccountsSink).value = emptyList()
    }
}