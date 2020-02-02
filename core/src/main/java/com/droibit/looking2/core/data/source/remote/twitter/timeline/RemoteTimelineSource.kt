package com.droibit.looking2.core.data.source.remote.twitter.timeline

import com.droibit.looking2.core.data.source.remote.twitter.api.AppTwitterApiClient
import com.droibit.looking2.core.data.source.remote.twitter.api.AppTwitterApiClientFactoryDelegate
import com.droibit.looking2.core.data.source.remote.twitter.api.await
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import javax.inject.Inject

class RemoteTimelineSource @Inject constructor(
    twitterCore: TwitterCore,
    private val mapper: TimelineMapper
) : AppTwitterApiClient.Factory by AppTwitterApiClientFactoryDelegate(twitterCore) {

    @Throws(TwitterError::class)
    suspend fun getHomeTimeline(session: TwitterSession, count: Int, sinceId: Long?): List<Tweet> {
        val apiClient = get(session)
        try {
            val timelineResponse = apiClient.statusesService.homeTimeline(
                count,
                sinceId,
                null,
                null,
                null,
                null,
                null
            ).await()
            return mapper.toTimeline(source = timelineResponse)
        } catch (e: TwitterException) {
            Timber.e(e)
            throw TwitterError(e)
        }
    }

    @Throws(TwitterError::class)
    suspend fun getMentionsTimeline(
        session: TwitterSession,
        count: Int,
        sinceId: Long?
    ): List<Tweet> {
        val apiClient = get(session)
        try {
            val timelineResponse = apiClient.statusesService.mentionsTimeline(
                count,
                sinceId,
                null,
                null,
                null,
                null
            ).await()
            return mapper.toTimeline(source = timelineResponse)
        } catch (e: TwitterException) {
            Timber.e(e)
            throw TwitterError(e)
        }
    }

    @Throws(TwitterError::class)
    suspend fun getUesrListTimeline(
        session: TwitterSession,
        listId: Long,
        count: Int,
        sinceId: Long?
    ): List<Tweet> {
        val apiClient = get(session)
        try {
            val timelineResponse = apiClient.userListService.statuses(
                listId,
                count,
                sinceId,
                null,
                null,
                null,
                null,
                null,
                null
            ).await()
            return mapper.toTimeline(source = timelineResponse)
        } catch (e: TwitterException) {
            Timber.e(e)
            throw TwitterError(e)
        }
    }
}