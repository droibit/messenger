package com.droibit.looking2.timeline.ui.content

import androidx.annotation.Keep
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
    class MyLists(val listId: Long) : TimelineSource()

    fun toGetCall(repository: TimelineRepository): GetCall = GetCall(this, repository)

    interface GetCall {

        @Throws(TwitterError::class)
        suspend operator fun invoke(sinceId: Long?): List<Tweet>

        companion object {
            operator fun invoke(
                source: TimelineSource,
                repository: TimelineRepository
            ): GetCall {
                return when (source) {
                    is Home -> object : GetCall {
                        override suspend fun invoke(sinceId: Long?): List<Tweet> {
                            return repository.getHomeTimeline(sinceId = sinceId)
                        }
                    }
                    is Mentions -> object : GetCall {
                        override suspend fun invoke(sinceId: Long?): List<Tweet> {
                            return repository.getMentionsTimeline(sinceId)
                        }
                    }
                    is MyLists -> object : GetCall {
                        override suspend fun invoke(sinceId: Long?): List<Tweet> {
                            return repository.getUserListTimeline(source.listId, sinceId)
                        }
                    }
                }
            }
        }
    }
}