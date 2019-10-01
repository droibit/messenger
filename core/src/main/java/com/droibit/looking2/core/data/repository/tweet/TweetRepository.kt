package com.droibit.looking2.core.data.repository.tweet

import com.droibit.looking2.core.model.tweet.RetweetError

interface TweetRepository {

    @Throws(RetweetError::class)
    suspend fun retweet(tweetId: Long)
}