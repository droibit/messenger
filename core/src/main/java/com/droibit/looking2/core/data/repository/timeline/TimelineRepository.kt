package com.droibit.looking2.core.data.repository.timeline

import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError

interface TimelineRepository {

    @Throws(TwitterError::class)
    suspend fun getHomeTimeline(count: Int, sinceId: Long?): List<Tweet>
}