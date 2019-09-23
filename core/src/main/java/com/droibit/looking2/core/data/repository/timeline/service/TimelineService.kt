package com.droibit.looking2.core.data.repository.timeline.service

import com.droibit.looking2.core.data.source.api.twitter.LookingTwitterApiClient
import com.droibit.looking2.core.data.source.api.twitter.await
import com.droibit.looking2.core.model.tweet.GetTimelineError
import com.droibit.looking2.core.model.tweet.Tweet
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class TimelineService @Inject constructor(
    private val twitterCore: TwitterCore,
    private val mapper: TimelineMapper
) {

    @Throws(GetTimelineError::class)
    suspend fun getHomeTimeline(session: TwitterSession, count: Int, sinceId: Long?): List<Tweet> {
        val apiClient = session.toApiClient()
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
        } catch (e: Exception) {
            Timber.e(e)
            throw if (e.cause is IOException) {
                GetTimelineError.Network()
            } else {
                GetTimelineError.UnExpected()
            }
        }
    }

    private fun TwitterSession.toApiClient(): LookingTwitterApiClient {
        val apiClient = twitterCore.getApiClient(this)
            ?: error("There is no api client corresponding to session($this).")
        return apiClient as LookingTwitterApiClient
    }
}