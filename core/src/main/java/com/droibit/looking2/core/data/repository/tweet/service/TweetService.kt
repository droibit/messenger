package com.droibit.looking2.core.data.repository.tweet.service

import com.droibit.looking2.core.data.source.api.twitter.LookingTwitterApiClient
import com.droibit.looking2.core.data.source.api.twitter.LookingTwitterApiClientFactoryDelegate
import com.droibit.looking2.core.data.source.api.twitter.await
import com.droibit.looking2.core.model.tweet.TweetActionError
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import java.io.IOException
import java.lang.Exception
import javax.inject.Inject

class TweetService @Inject constructor(
    private val twitterCore: TwitterCore
): LookingTwitterApiClient.Factory by LookingTwitterApiClientFactoryDelegate(twitterCore) {

    @Throws(TweetActionError::class)
    suspend fun retweet(session: TwitterSession, tweetId: Long) {
        val apiClient = get(session)
        try {
            apiClient.statusesService.retweet(tweetId, null).await()
        } catch (e: Exception) {
            Timber.e(e)
            throw e.toTweetActionError()
        }
    }

    @Throws(TweetActionError::class)
    suspend fun likeTweet(session: TwitterSession, tweetId: Long) {
        val apiClient = get(session)
        try {
            apiClient.favoriteService.create(tweetId, null).await()
        } catch (e: Exception) {
            Timber.e(e)
            throw e.toTweetActionError()
        }
    }

    private fun Exception.toTweetActionError(): TweetActionError {
        return if (this.cause is IOException) {
            TweetActionError.Network()
        } else {
            TweetActionError.UnExpected()
        }
    }
}