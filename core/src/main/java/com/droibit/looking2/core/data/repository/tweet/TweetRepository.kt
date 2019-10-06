package com.droibit.looking2.core.data.repository.tweet

import com.droibit.looking2.core.model.tweet.TwitterError

interface TweetRepository {

    @Throws(TwitterError::class)
    suspend fun retweet(tweetId: Long)

    @Throws(TwitterError::class)
    suspend fun likeTweet(tweetId: Long)
}