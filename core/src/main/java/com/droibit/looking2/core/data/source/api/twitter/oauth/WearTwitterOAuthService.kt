package com.droibit.looking2.core.data.source.api.twitter.oauth

import android.net.Uri
import android.support.wearable.authentication.OAuthClient
import androidx.annotation.UiThread
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.internal.TwitterApi
import com.twitter.sdk.android.core.internal.oauth.OAuth1aService
import com.twitter.sdk.android.core.internal.oauth.OAuthResponse
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class WearTwitterOAuthService @Inject constructor(
    twitterCore: TwitterCore,
    api: TwitterApi,
    @Named("wearCallbackUrl") private val callbackUrl: String
) : OAuth1aService(twitterCore, api) {

    override fun buildCallbackUrl(authConfig: TwitterAuthConfig): String {
        return callbackUrl
    }

    @Throws(TwitterException::class)
    suspend fun requestTempToken(): OAuthResponse = suspendCancellableCoroutine { cont ->
        requestTempToken(object : Callback<OAuthResponse>() {
            override fun success(result: Result<OAuthResponse>) {
                if (cont.isActive) cont.resume(result.data)
            }

            override fun failure(exception: TwitterException) {
                if (cont.isActive) cont.resumeWithException(exception)
            }
        })
    }

    @UiThread
    @Throws(ApiException::class)
    suspend fun sendAuthorizationRequest(
        client: OAuthClient,
        requestToken: TwitterAuthToken
    ): String =
        suspendCancellableCoroutine { cont ->
            val authorizeUrl = getAuthorizeUrl(requestToken)
            client.sendAuthorizationRequest(
                Uri.parse(authorizeUrl),
                object : OAuthClient.Callback() {
                    override fun onAuthorizationError(errorCode: Int) {
                        if (cont.isActive) cont.resumeWithException(ApiException(Status(errorCode)))
                    }

                    override fun onAuthorizationResponse(
                        requestUrl: Uri,
                        responseUrl: Uri
                    ) {
                        if (cont.isActive) cont.resume(responseUrl.toString())
                    }
                })
        }

    @Throws(TwitterException::class)
    suspend fun requestAccessToken(
        requestToken: TwitterAuthToken,
        authorizationResponseUrl: String
    ): OAuthResponse = suspendCancellableCoroutine { cont ->
        val oauthVerifier = Uri.parse(authorizationResponseUrl)
            .getQueryParameter("oauth_verifier")
        requestAccessToken(object : Callback<OAuthResponse>() {
            override fun success(result: Result<OAuthResponse>) {
                if (cont.isActive) cont.resume(result.data)
            }

            override fun failure(exception: TwitterException) {
                if (cont.isActive) cont.resumeWithException(exception)
            }
        }, requestToken, oauthVerifier)
    }
}