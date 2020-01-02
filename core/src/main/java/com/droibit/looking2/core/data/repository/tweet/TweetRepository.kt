package com.droibit.looking2.core.data.repository.tweet

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.tweet.service.TweetService
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterStore
import com.droibit.looking2.core.model.tweet.TwitterError
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TweetRepository @Inject constructor(
    private val tweetService: TweetService,
    private val localStore: LocalTwitterStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    @Throws(TwitterError::class)
    suspend fun tweet(text: String, inReplyToId: Long? = null) {
        withContext(dispatcherProvider.io) {
            val session = localStore.getActiveSession() ?: throw TwitterError.Unauthorized
            tweetService.tweet(session, text, inReplyToId)
        }
    }

    @Throws(TwitterError::class)
    suspend fun retweet(tweetId: Long) {
        withContext(dispatcherProvider.io) {
            val session = localStore.getActiveSession() ?: throw TwitterError.Unauthorized
            tweetService.retweet(session, tweetId)
        }
    }

    @Throws(TwitterError::class)
    suspend fun likeTweet(tweetId: Long) {
        withContext(dispatcherProvider.io) {
            val session = localStore.getActiveSession() ?: throw TwitterError.Unauthorized
            tweetService.likeTweet(session, tweetId)
        }
    }
}