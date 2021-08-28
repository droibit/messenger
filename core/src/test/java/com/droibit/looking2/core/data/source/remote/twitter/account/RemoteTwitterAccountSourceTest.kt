package com.droibit.looking2.core.data.source.remote.twitter.account

import androidx.wear.phone.interactions.authentication.RemoteAuthClient
import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.remote.twitter.api.AppTwitterApiClient
import com.droibit.looking2.core.data.source.remote.twitter.api.oauth.PhoneAuthenticationException
import com.droibit.looking2.core.data.source.remote.twitter.api.oauth.WearTwitterOAuthService
import com.droibit.looking2.core.model.account.AuthenticationError
import com.google.common.truth.Truth.assertThat
import com.twitter.sdk.android.core.TwitterAuthException
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.internal.oauth.OAuthResponse
import javax.inject.Provider
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class RemoteTwitterAccountSourceTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var twitterCore: TwitterCore

    @Mock
    private lateinit var oAuthService: WearTwitterOAuthService

    @Mock
    private lateinit var apiClientFactory: AppTwitterApiClient.Factory

    @Mock
    private lateinit var oauthClientProvider: Provider<RemoteAuthClient>

    @Mock
    private lateinit var dispatcherProvider: CoroutinesDispatcherProvider

    @InjectMocks
    private lateinit var remoteSource: RemoteTwitterAccountSource

    @Test
    fun ensureApiClient() {
        val apiClient = mock<AppTwitterApiClient>()
        whenever(apiClientFactory.get(any())).thenReturn(apiClient)

        val session = mock<TwitterSession>()
        remoteSource.ensureApiClient(session)

        verify(twitterCore).addApiClient(session, apiClient)
    }

    @Test
    fun requestTempToken_success() = runBlockingTest {
        val authToken = mock<TwitterAuthToken>()
        val response = OAuthResponse(authToken, "user1", 1L)
        whenever(oAuthService.requestTempToken()).thenReturn(response)

        val tempToken = remoteSource.requestTempToken()
        assertThat(tempToken).isEqualTo(authToken)

        verify(oAuthService).requestTempToken()
    }

    @Test
    fun requestTempToken_error() = runBlockingTest {
        val error = mock<TwitterAuthException>()
        whenever(oAuthService.requestTempToken()).thenThrow(error)

        try {
            remoteSource.requestTempToken()
            fail("error")
        } catch (e: AuthenticationError) {
            assertThat(e).isEqualTo(AuthenticationError.UnExpected)
        }

        verify(oAuthService).requestTempToken()
    }

    @Test
    fun sendAuthorizationRequest_success() = runBlockingTest {
        whenever(dispatcherProvider.main).thenReturn(TestCoroutineDispatcher())

        val client = mock<RemoteAuthClient>()
        whenever(oauthClientProvider.get()).thenReturn(client)

        val responseUrl = "url"
        whenever(oAuthService.sendAuthorizationRequest(any(), any()))
            .thenReturn(responseUrl)

        val authToken = mock<TwitterAuthToken>()
        val actualResponseUrl = remoteSource.sendAuthorizationRequest(authToken)
        assertThat(actualResponseUrl).isEqualTo(responseUrl)

        verify(oAuthService).sendAuthorizationRequest(client, authToken)
        verify(client).close()
    }

    @Test
    fun sendAuthorizationRequest_error() = runBlockingTest {
        whenever(dispatcherProvider.main).thenReturn(TestCoroutineDispatcher())

        val client = mock<RemoteAuthClient>()
        whenever(oauthClientProvider.get()).thenReturn(client)

        val errorCode = RemoteAuthClient.ERROR_UNSUPPORTED
        val error = PhoneAuthenticationException(errorCode)
        whenever(oAuthService.sendAuthorizationRequest(any(), any()))
            .thenThrow(error)

        val authToken = mock<TwitterAuthToken>()
        try {
            remoteSource.sendAuthorizationRequest(authToken)
            fail("error")
        } catch (e: AuthenticationError) {
            assertThat(e).isInstanceOf(AuthenticationError.Phone::class.java)

            val actualError = e as AuthenticationError.Phone
            assertThat(actualError.errorCode).isEqualTo(errorCode)
        }

        verify(oAuthService).sendAuthorizationRequest(client, authToken)
        verify(client).close()
    }

    @Test
    fun createNewSession_success() = runBlockingTest {
        val authToken = mock<TwitterAuthToken>()
        val response = OAuthResponse(authToken, "test", 1L)
        whenever(oAuthService.requestAccessToken(any(), anyString()))
            .thenReturn(response)

        val responseUrl = "url"
        val newSession = remoteSource.createNewSession(authToken, responseUrl)
        assertThat(newSession.authToken).isEqualTo(authToken)
        assertThat(newSession.userId).isEqualTo(response.userId)
        assertThat(newSession.userName).isEqualTo(response.userName)

        verify(oAuthService).requestAccessToken(authToken, responseUrl)
    }

    @Test
    fun createNewSession_error() = runBlockingTest {
        val error = mock<TwitterException>()
        whenever(oAuthService.requestAccessToken(any(), anyString()))
            .thenThrow(error)

        val authToken = mock<TwitterAuthToken>()
        val responseUrl = "url"

        try {
            remoteSource.createNewSession(authToken, responseUrl)
            fail("error")
        } catch (e: AuthenticationError) {
            assertThat(e).isEqualTo(AuthenticationError.UnExpected)
        }

        verify(oAuthService).requestAccessToken(authToken, responseUrl)
    }
}
