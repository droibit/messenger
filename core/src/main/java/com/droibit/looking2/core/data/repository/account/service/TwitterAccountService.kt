package com.droibit.looking2.core.data.repository.account.service

import android.support.wearable.authentication.OAuthClient
import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.api.twitter.oauth.WearTwitterOAuthService
import com.droibit.looking2.core.model.account.AuthenticationError
import com.google.android.gms.common.api.ApiException
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Provider

class TwitterAccountService @Inject constructor(
    private val oauthClientProvider: Provider<OAuthClient>,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val api: WearTwitterOAuthService
) {

    @Throws(AuthenticationError::class)
    suspend fun requestTempToken(): TwitterAuthToken {
        try {
            val result = api.requestTempToken()
            return result.authToken
        } catch (e: TwitterException) {
            Timber.e(e)
            throw if (e.cause is IOException) {
                AuthenticationError.Network()
            } else {
                AuthenticationError.UnExpected()
            }
        }
    }

    @Throws(AuthenticationError::class)
    suspend fun sendAuthorizationRequest(requestToken: TwitterAuthToken): String {
        return withContext(dispatcherProvider.main) {
            val client = oauthClientProvider.get()
            try {
                api.sendAuthorizationRequest(client, requestToken)
            } catch (e: ApiException) {
                throw AuthenticationError.PlayServices(statusCode = e.statusCode)
            } finally {
                client.destroy()
            }
        }
    }

    @Throws(AuthenticationError::class)
    suspend fun createNewSession(
        requestToken: TwitterAuthToken,
        authorizationResponseUrl: String
    ): TwitterSession {
        try {
            val result = api.requestAccessToken(requestToken, authorizationResponseUrl)
            return TwitterSession(result.authToken, result.userId, result.userName)
        } catch (e: TwitterException) {
            Timber.e(e)
            throw if (e.cause is IOException) {
                AuthenticationError.Network()
            } else {
                AuthenticationError.UnExpected()
            }
        }
    }
}