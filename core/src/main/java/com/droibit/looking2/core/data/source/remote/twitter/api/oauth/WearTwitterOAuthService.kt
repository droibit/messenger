package com.droibit.looking2.core.data.source.remote.twitter.api.oauth

import android.content.Context
import android.net.Uri
import androidx.annotation.UiThread
import androidx.wear.phone.interactions.authentication.CodeChallenge
import androidx.wear.phone.interactions.authentication.CodeVerifier
import androidx.wear.phone.interactions.authentication.RemoteAuthClient
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
import androidx.wear.phone.interactions.authentication.OAuthRequest as WearOAuthRequest
import androidx.wear.phone.interactions.authentication.OAuthResponse as WearOAuthResponse

class WearTwitterOAuthService @Inject constructor(
    @Named("appContext") private val context: Context,
    twitterCore: TwitterCore,
    api: TwitterApi,
) : OAuth1aService(twitterCore, api) {

    override fun buildCallbackUrl(authConfig: TwitterAuthConfig): String {
        return WearOAuthRequest.WEAR_REDIRECT_URL_PREFIX + context.packageName
    }

    @Throws(TwitterException::class)
    suspend fun requestTempToken(): OAuthResponse = suspendCancellableCoroutine { cont ->
        requestTempToken(
            object : Callback<OAuthResponse>() {
                override fun success(result: Result<OAuthResponse>) {
                    if (cont.isActive) cont.resume(result.data)
                }

                override fun failure(exception: TwitterException) {
                    if (cont.isActive) cont.resumeWithException(exception)
                }
            }
        )
    }

    @UiThread
    @Throws(PhoneAuthenticationException::class)
    suspend fun sendAuthorizationRequest(
        client: RemoteAuthClient,
        requestToken: TwitterAuthToken
    ): String =
        suspendCancellableCoroutine { cont ->
            val authorizeUrlString = getAuthorizeUrl(requestToken)
            val request = WearOAuthRequest.Builder(context)
                .setAuthProviderUrl(Uri.parse(authorizeUrlString))
                // Specify only to bypass OAuthRequest validation.
                .setCodeChallenge(CodeChallenge(CodeVerifier()))
                // Specify only to bypass OAuthRequest validation.
                .setClientId("")
                .build()
            client.sendAuthorizationRequest(
                request = request,
                executor = { command -> command?.run() },
                clientCallback = object : RemoteAuthClient.Callback() {
                    override fun onAuthorizationError(request: WearOAuthRequest, errorCode: Int) {
                        if (cont.isActive) {
                            cont.resumeWithException(PhoneAuthenticationException(errorCode))
                        }
                    }

                    override fun onAuthorizationResponse(
                        request: WearOAuthRequest,
                        response: WearOAuthResponse
                    ) {
                        if (cont.isActive) {
                            when (val errorCode = response.errorCode) {
                                RemoteAuthClient.NO_ERROR -> {
                                    cont.resume(requireNotNull(response.responseUrl).toString())
                                }
                                else -> {
                                    cont.resumeWithException(PhoneAuthenticationException(errorCode))
                                }
                            }
                        }
                    }
                }
            )
        }

    @Throws(TwitterException::class)
    suspend fun requestAccessToken(
        requestToken: TwitterAuthToken,
        authorizationResponseUrl: String
    ): OAuthResponse = suspendCancellableCoroutine { cont ->
        val oauthVerifier = Uri.parse(authorizationResponseUrl)
            .getQueryParameter("oauth_verifier")
        requestAccessToken(
            object : Callback<OAuthResponse>() {
                override fun success(result: Result<OAuthResponse>) {
                    if (cont.isActive) cont.resume(result.data)
                }

                override fun failure(exception: TwitterException) {
                    if (cont.isActive) cont.resumeWithException(exception)
                }
            },
            requestToken,
            oauthVerifier
        )
    }
}
