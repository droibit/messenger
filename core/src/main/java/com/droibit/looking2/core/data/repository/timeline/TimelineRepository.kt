package com.droibit.looking2.core.data.repository.timeline

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.timeline.service.TimelineService
import com.droibit.looking2.core.data.source.local.twitter.TwitterLocalStore
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimelineRepository @Inject constructor(
    private val timelineService: TimelineService,
    private val localStore: TwitterLocalStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    @Throws(TwitterError::class)
    suspend fun getHomeTimeline(count: Int, sinceId: Long?): List<Tweet> {
        return withContext(dispatcherProvider.io) {
            val session = localStore.activeSession() ?: throw TwitterError.Unauthorized
            timelineService.getHomeTimeline(session, count, sinceId)
        }
    }

    @Throws(TwitterError::class)
    suspend fun getUserListTimeline(listId: Long, sinceId: Long?): List<Tweet> {
        return withContext(dispatcherProvider.io) {
            val session = localStore.activeSession() ?: throw TwitterError.Unauthorized
            // TODO: load count from local store.
            timelineService.getUesrListTimeline(session, listId, 30, sinceId)
        }
    }
}