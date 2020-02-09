package com.droibit.looking2.core.data.repository.timeline

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterSource
import com.droibit.looking2.core.data.source.local.usersettings.LocalUserSettingsSource
import com.droibit.looking2.core.data.source.remote.twitter.timeline.RemoteTimelineSource
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimelineRepository @Inject constructor(
    private val remoteTimelineSource: RemoteTimelineSource,
    private val localTwitterSource: LocalTwitterSource,
    private val localUserSettingsSource: LocalUserSettingsSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    @Throws(TwitterError::class)
    suspend fun getHomeTimeline(sinceId: Long?): List<Tweet> {
        return withContext(dispatcherProvider.io) {
            val session = localTwitterSource.activeSession ?: throw TwitterError.Unauthorized
            remoteTimelineSource.getHomeTimeline(
                session,
                localUserSettingsSource.numOfTweets,
                sinceId
            )
        }
    }

    @Throws(TwitterError::class)
    suspend fun getMentionsTimeline(sinceId: Long?): List<Tweet> {
        return withContext(dispatcherProvider.io) {
            val session = localTwitterSource.activeSession ?: throw TwitterError.Unauthorized
            remoteTimelineSource.getMentionsTimeline(
                session,
                localUserSettingsSource.numOfTweets,
                sinceId
            )
        }
    }

    @Throws(TwitterError::class)
    suspend fun getUserListTimeline(listId: Long, sinceId: Long?): List<Tweet> {
        return withContext(dispatcherProvider.io) {
            val session = localTwitterSource.activeSession ?: throw TwitterError.Unauthorized
            remoteTimelineSource.getUserListTimeline(
                session,
                listId,
                localUserSettingsSource.numOfTweets,
                sinceId
            )
        }
    }
}