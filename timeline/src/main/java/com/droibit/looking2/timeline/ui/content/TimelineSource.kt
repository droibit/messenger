package com.droibit.looking2.timeline.ui.content

import androidx.annotation.Keep
import androidx.annotation.VisibleForTesting
import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import java.io.Serializable

sealed class TimelineSource : Serializable {

    @Keep
    object Home : TimelineSource()

    @Keep
    object Mentions : TimelineSource()

    @Keep
    data class MyLists(val listId: Long) : TimelineSource()

    interface GetCall {

        @Throws(TwitterError::class)
        suspend operator fun invoke(sinceId: Long?): List<Tweet>

        companion object {
            operator fun invoke(source: TimelineSource, repository: TimelineRepository): GetCall {
                return when (source) {
                    is Home -> GetHomeTimelineCall(repository)
                    is Mentions -> GetMentionsTimelineCall(repository)
                    is MyLists -> GetMyListsTimelineCall(source.listId, repository)
                }
            }
        }
    }
}

class GetHomeTimelineCall(
    private val repository: TimelineRepository
) : TimelineSource.GetCall {

    override suspend fun invoke(sinceId: Long?): List<Tweet> {
        return repository.getHomeTimeline(sinceId)
    }
}

class GetMentionsTimelineCall(
    private val repository: TimelineRepository
) : TimelineSource.GetCall {

    override suspend fun invoke(sinceId: Long?): List<Tweet> {
        return repository.getMentionsTimeline(sinceId)
    }
}

class GetMyListsTimelineCall(
    @VisibleForTesting val listId: Long,
    private val repository: TimelineRepository
) : TimelineSource.GetCall {

    override suspend fun invoke(sinceId: Long?): List<Tweet> {
        return repository.getUserListTimeline(listId, sinceId)
    }
}