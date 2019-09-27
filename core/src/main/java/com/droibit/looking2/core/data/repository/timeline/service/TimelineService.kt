package com.droibit.looking2.core.data.repository.timeline.service

import com.droibit.looking2.core.data.source.api.twitter.LookingTwitterApiClient
import com.droibit.looking2.core.data.source.api.twitter.LookingTwitterApiClientFactoryDelegate
import com.droibit.looking2.core.data.source.api.twitter.await
import com.droibit.looking2.core.model.tweet.GetTimelineError
import com.droibit.looking2.core.model.tweet.Tweet
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class TimelineService @Inject constructor(
    twitterCore: TwitterCore,
    private val mapper: TimelineMapper
):  LookingTwitterApiClient.Factory by LookingTwitterApiClientFactoryDelegate(twitterCore) {

    @Throws(GetTimelineError::class)
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
        } catch (e: Exception) {
            Timber.e(e)
            throw if (e.cause is IOException) {
                GetTimelineError.Network()
            } else {
                GetTimelineError.UnExpected()
            }
        }
    }
}