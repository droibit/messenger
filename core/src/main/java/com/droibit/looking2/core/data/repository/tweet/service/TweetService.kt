package com.droibit.looking2.core.data.repository.tweet.service

import com.droibit.looking2.core.data.source.api.twitter.LookingTwitterApiClient
import com.droibit.looking2.core.data.source.api.twitter.LookingTwitterApiClientFactoryDelegate
import com.droibit.looking2.core.model.tweet.RetweetError
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class TweetService @Inject constructor(
    private val twitterCore: TwitterCore
): LookingTwitterApiClient.Factory by LookingTwitterApiClientFactoryDelegate(twitterCore) {

    @Throws(RetweetError::class)
    suspend fun retweet(session: TwitterSession, tweetId: Long) {
        val apiClient = get(session)
        try {
            apiClient.statusesService.retweet(tweetId, null)
        } catch (e: Exception) {
            Timber.e(e)
            throw if (e.cause is IOException) {
                RetweetError.Network()
            } else {
                RetweetError.UnExpected()
            }
        }
    }
}