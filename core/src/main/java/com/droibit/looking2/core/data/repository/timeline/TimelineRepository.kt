package com.droibit.looking2.core.data.repository.timeline

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.timeline.service.TimelineService
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterStore
import com.droibit.looking2.core.data.source.local.usersettings.LocalUserSettingsStore
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimelineRepository @Inject constructor(
    private val timelineService: TimelineService,
    private val localTwitterStore: LocalTwitterStore,
    private val localUserSettingsStore: LocalUserSettingsStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    @Throws(TwitterError::class)
    suspend fun getHomeTimeline(sinceId: Long?): List<Tweet> {
        return withContext(dispatcherProvider.io) {
            val session = localTwitterStore.activeSession() ?: throw TwitterError.Unauthorized
            timelineService.getHomeTimeline(session, localUserSettingsStore.numOfTweets, sinceId)
        }
    }

    @Throws(TwitterError::class)
    suspend fun getMentionsTimeline(sinceId: Long?): List<Tweet> {
        return withContext(dispatcherProvider.io) {
            val session = localTwitterStore.activeSession() ?: throw TwitterError.Unauthorized
            timelineService.getMentionsTimeline(
                session,
                localUserSettingsStore.numOfTweets,
                sinceId
            )
        }
    }

    @Throws(TwitterError::class)
    suspend fun getUserListTimeline(listId: Long, sinceId: Long?): List<Tweet> {
        return withContext(dispatcherProvider.io) {
            val session = localTwitterStore.activeSession() ?: throw TwitterError.Unauthorized
            timelineService.getUesrListTimeline(
                session,
                listId,
                localUserSettingsStore.numOfTweets,
                sinceId
            )
        }
    }
}