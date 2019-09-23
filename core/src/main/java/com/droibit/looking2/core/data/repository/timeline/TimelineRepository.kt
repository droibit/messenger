package com.droibit.looking2.core.data.repository.timeline

import com.droibit.looking2.core.model.tweet.Tweet

interface TimelineRepository {

    suspend fun getHomeTimeline(count: Int, sinceId: Long?): List<Tweet>
}