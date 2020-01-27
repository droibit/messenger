package com.droibit.looking2.timeline.ui.content

import androidx.annotation.Keep
import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import java.io.Serializable

sealed class TimelineSource : Serializable {

    abstract fun toGetCall(repository: TimelineRepository): GetCall

    @Keep
    object Home : TimelineSource() {
        override fun toGetCall(repository: TimelineRepository): GetCall {
            return object : GetCall {
                override suspend fun invoke(sinceId: Long?): List<Tweet> {
                    return repository.getHomeTimeline(sinceId = sinceId)
                }
            }
        }
    }

    @Keep
    object Mentions : TimelineSource() {
        override fun toGetCall(repository: TimelineRepository): GetCall {
            return object : GetCall {
                override suspend fun invoke(sinceId: Long?): List<Tweet> {
                    return repository.getMentionsTimeline(sinceId)
                }
            }
        }
    }

    @Keep
    class MyLists(val listId: Long) : TimelineSource() {
        override fun toGetCall(repository: TimelineRepository): GetCall {
            return object : GetCall {
                override suspend fun invoke(sinceId: Long?): List<Tweet> {
                    return repository.getUserListTimeline(listId, sinceId)
                }
            }
        }
    }

    interface GetCall {

        @Throws(TwitterError::class)
        suspend operator fun invoke(sinceId: Long?): List<Tweet>
    }
}