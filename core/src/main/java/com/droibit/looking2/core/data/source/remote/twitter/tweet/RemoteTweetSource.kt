package com.droibit.looking2.core.data.source.remote.twitter.tweet

import com.droibit.looking2.core.data.source.remote.twitter.api.AppTwitterApiClient
import com.droibit.looking2.core.data.source.remote.twitter.api.AppTwitterApiClientFactoryDelegate
import com.droibit.looking2.core.data.source.remote.twitter.api.await
import com.droibit.looking2.core.model.tweet.TwitterError
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import javax.inject.Inject
import timber.log.Timber

private const val ERROR_CODE_ALREADY_FAVORITE = 139
private const val ERROR_CODE_ALREADY_RETWEET = 327

class RemoteTweetSource @Inject constructor(
    private val twitterCore: TwitterCore
) : AppTwitterApiClient.Factory by AppTwitterApiClientFactoryDelegate(twitterCore) {

    @Throws(TwitterError::class)
    suspend fun tweet(session: TwitterSession, text: String, inReplyToId: Long?) {
        val apiClient = get(session)
        try {
            apiClient.statusesService.update(
                text,
                inReplyToId,
                null,
                null,
                null,
                null,
                null,
                null,
                null
            ).await()
        } catch (e: TwitterException) {
            Timber.e(e)
            throw TwitterError(e)
        }
    }

    @Throws(TwitterError::class)
    suspend fun retweet(session: TwitterSession, tweetId: Long) {
        val apiClient = get(session)
        try {
            apiClient.statusesService.retweet(tweetId, null).await()
        } catch (e: TwitterException) {
            Timber.e(e)
            val error = TwitterError(e)
            if (error is TwitterError.UnExpected &&
                error.errorCode == ERROR_CODE_ALREADY_RETWEET
            ) {
                return
            }
            throw error
        }
    }

    @Throws(TwitterError::class)
    suspend fun likeTweet(session: TwitterSession, tweetId: Long) {
        val apiClient = get(session)
        try {
            apiClient.favoriteService.create(tweetId, null).await()
        } catch (e: TwitterException) {
            Timber.e(e)
            val error = TwitterError(e)
            if (error is TwitterError.UnExpected &&
                error.errorCode == ERROR_CODE_ALREADY_FAVORITE
            ) {
                return
            }
            throw error
        }
    }
}
