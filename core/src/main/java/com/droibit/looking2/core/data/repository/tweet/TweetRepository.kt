package com.droibit.looking2.core.data.repository.tweet

interface TweetRepository {

    @Throws
    suspend fun retweet(tweetId: Long)
}