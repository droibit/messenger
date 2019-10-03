package com.droibit.looking2.core.data.repository.tweet

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.tweet.service.TweetService
import com.droibit.looking2.core.data.source.local.twitter.TwitterLocalStore
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TweetRepositoryImpl @Inject constructor(
    private val tweetService: TweetService,
    private val localStore: TwitterLocalStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : TweetRepository {

    override suspend fun retweet(tweetId: Long) {
        withContext(dispatcherProvider.io) {
            val activeSession = requireNotNull(localStore.activeSession())
            tweetService.retweet(activeSession, tweetId)
        }
    }

    override suspend fun likeTweet(tweetId: Long) {
        withContext(dispatcherProvider.io) {
            val activeSession = requireNotNull(localStore.activeSession())
            tweetService.likeTweet(activeSession, tweetId)
        }
    }
}