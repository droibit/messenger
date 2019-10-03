package com.droibit.looking2.core.data.repository.tweet

import com.droibit.looking2.core.model.tweet.TweetActionError

interface TweetRepository {

    @Throws(TweetActionError::class)
    suspend fun retweet(tweetId: Long)
}