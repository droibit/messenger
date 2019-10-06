package com.droibit.looking2.core.data.repository.tweet.service

import com.droibit.looking2.core.data.source.api.twitter.AppTwitterApiClient
import com.droibit.looking2.core.data.source.api.twitter.AppTwitterApiClientFactoryDelegate
import com.droibit.looking2.core.data.source.api.twitter.await
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.toTwitterError
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import javax.inject.Inject

class TweetService @Inject constructor(
    private val twitterCore: TwitterCore
) : AppTwitterApiClient.Factory by AppTwitterApiClientFactoryDelegate(twitterCore) {

    @Throws(TwitterError::class)
    suspend fun retweet(session: TwitterSession, tweetId: Long) {
        val apiClient = get(session)
        try {
            apiClient.statusesService.retweet(tweetId, null).await()
        } catch (e: TwitterException) {
            Timber.e(e)
            throw e.toTwitterError()
        }
    }

    @Throws(TwitterError::class)
    suspend fun likeTweet(session: TwitterSession, tweetId: Long) {
        val apiClient = get(session)
        try {
            apiClient.favoriteService.create(tweetId, null).await()
        } catch (e: TwitterException) {
            Timber.e(e)
            throw e.toTwitterError()
        }
    }
}