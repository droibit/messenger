package com.droibit.looking2.core.data.repository.tweet

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.remote.twitter.tweet.RemoteTweetSource
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterSource
import com.droibit.looking2.core.model.tweet.TwitterError
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TweetRepository @Inject constructor(
    private val remoteSource: RemoteTweetSource,
    private val localSource: LocalTwitterSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    @Throws(TwitterError::class)
    suspend fun tweet(text: String, inReplyToId: Long? = null) {
        withContext(dispatcherProvider.io) {
            val session = localSource.activeSession ?: throw TwitterError.Unauthorized
            remoteSource.tweet(session, text, inReplyToId)
        }
    }

    @Throws(TwitterError::class)
    suspend fun retweet(tweetId: Long) {
        withContext(dispatcherProvider.io) {
            val session = localSource.activeSession ?: throw TwitterError.Unauthorized
            remoteSource.retweet(session, tweetId)
        }
    }

    @Throws(TwitterError::class)
    suspend fun likeTweet(tweetId: Long) {
        withContext(dispatcherProvider.io) {
            val session = localSource.activeSession ?: throw TwitterError.Unauthorized
            remoteSource.likeTweet(session, tweetId)
        }
    }
}