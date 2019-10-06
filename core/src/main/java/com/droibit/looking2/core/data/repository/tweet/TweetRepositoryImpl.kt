package com.droibit.looking2.core.data.repository.tweet

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.tweet.service.TweetService
import com.droibit.looking2.core.data.source.local.twitter.TwitterLocalStore
import com.droibit.looking2.core.model.tweet.TwitterError
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TweetRepositoryImpl @Inject constructor(
    private val tweetService: TweetService,
    private val localStore: TwitterLocalStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : TweetRepository {

    override suspend fun retweet(tweetId: Long) {
        withContext(dispatcherProvider.io) {
            val session = localStore.activeSession() ?: throw TwitterError.Unauthorized
            tweetService.retweet(session, tweetId)
        }
    }

    override suspend fun likeTweet(tweetId: Long) {
        withContext(dispatcherProvider.io) {
            val session = localStore.activeSession() ?: throw TwitterError.Unauthorized
            tweetService.likeTweet(session, tweetId)
        }
    }
}