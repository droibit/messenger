package com.droibit.looking2.core.data.repository.timeline.service

import com.droibit.looking2.core.data.source.api.twitter.LookingTwitterApiClient
import com.droibit.looking2.core.data.source.api.twitter.LookingTwitterApiClientFactoryDelegate
import com.droibit.looking2.core.data.source.api.twitter.await
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.toTwitterError
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import javax.inject.Inject

class TimelineService @Inject constructor(
    twitterCore: TwitterCore,
    private val mapper: TimelineMapper
) : LookingTwitterApiClient.Factory by LookingTwitterApiClientFactoryDelegate(twitterCore) {

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
            throw e.toTwitterError()
        }
    }
}