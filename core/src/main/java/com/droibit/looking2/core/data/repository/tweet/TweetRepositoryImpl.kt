package com.droibit.looking2.core.data.repository.tweet

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.local.twitter.TwitterLocalStore
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TweetRepositoryImpl @Inject constructor(
    private val localStore: TwitterLocalStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : TweetRepository {

    override suspend fun retweet(tweetId: Long) {
        withContext<Unit>(dispatcherProvider.io) {
            TODO("not implemented")
        }
    }
}