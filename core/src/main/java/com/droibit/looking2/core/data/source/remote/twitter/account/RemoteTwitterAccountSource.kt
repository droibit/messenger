package com.droibit.looking2.core.data.source.remote.twitter.account

import androidx.wear.phone.interactions.authentication.RemoteAuthClient
import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.remote.twitter.api.AppTwitterApiClient
import com.droibit.looking2.core.data.source.remote.twitter.api.oauth.PhoneAuthenticationException
import com.droibit.looking2.core.data.source.remote.twitter.api.oauth.WearTwitterOAuthService
import com.droibit.looking2.core.model.account.AuthenticationError
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
class RemoteTwitterAccountSource @Inject constructor(
    private val twitterCore: TwitterCore,
    private val oAuthService: WearTwitterOAuthService,
    private val apiClientFactory: AppTwitterApiClient.Factory,
    private val oauthClientProvider: Provider<RemoteAuthClient>,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    fun ensureApiClient(session: TwitterSession) {
        twitterCore.addApiClient(session, apiClientFactory.get(session))
    }

    @Throws(AuthenticationError::class)
    suspend fun requestTempToken(): TwitterAuthToken {
        try {
            val result = oAuthService.requestTempToken()
            return result.authToken
        } catch (e: TwitterException) {
            Timber.e(e)
            throw AuthenticationError(e)
        }
    }

    @Throws(AuthenticationError::class)
    suspend fun sendAuthorizationRequest(requestToken: TwitterAuthToken): String {
        return withContext(dispatcherProvider.main) {
            val client = oauthClientProvider.get()
            try {
                oAuthService.sendAuthorizationRequest(client, requestToken)
            } catch (e: PhoneAuthenticationException) {
                Timber.e(e)
                throw AuthenticationError.Phone(e.errorCode)
            } finally {
                client.close()
            }
        }
    }

    @Throws(AuthenticationError::class)
    suspend fun createNewSession(
        requestToken: TwitterAuthToken,
        authorizationResponseUrl: String
    ): TwitterSession {
        try {
            val result = oAuthService.requestAccessToken(requestToken, authorizationResponseUrl)
            return TwitterSession(result.authToken, result.userId, result.userName)
        } catch (e: TwitterException) {
            Timber.e(e)
            throw AuthenticationError(e)
        }
    }
}
