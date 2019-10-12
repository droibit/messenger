package com.droibit.looking2.timeline.ui.content

import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import java.io.Serializable

sealed class TimelineSource : Serializable {
    object Home : TimelineSource()
    object Mentions : TimelineSource()
    class MyLists(val listId: Long) : TimelineSource()
}

interface GetTimelineCall {

    @Throws(TwitterError::class)
    suspend fun execute(sinceId: Long?): List<Tweet>

    class Factory(private val repository: TimelineRepository) {

        fun create(source: TimelineSource): GetTimelineCall {
            return when (source) {
                is TimelineSource.Home -> object : GetTimelineCall {
                    override suspend fun execute(sinceId: Long?): List<Tweet> {
                        return repository.getHomeTimeline(sinceId = sinceId)
                    }
                }
                is TimelineSource.Mentions -> object : GetTimelineCall {
                    override suspend fun execute(sinceId: Long?): List<Tweet> {
                        return repository.getMentionsTimeline(sinceId)
                    }
                }
                is TimelineSource.MyLists -> object : GetTimelineCall {
                    override suspend fun execute(sinceId: Long?): List<Tweet> {
                        return repository.getUserListTimeline(source.listId, sinceId)
                    }
                }
            }
        }
    }
}