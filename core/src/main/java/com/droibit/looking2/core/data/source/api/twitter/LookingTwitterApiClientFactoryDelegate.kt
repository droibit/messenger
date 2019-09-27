package com.droibit.looking2.core.data.source.api.twitter

import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession

class LookingTwitterApiClientFactoryDelegate(
    private val twitterCore: TwitterCore
): LookingTwitterApiClient.Factory {
    override fun get(session: TwitterSession): LookingTwitterApiClient {
        val apiClient = twitterCore.getApiClient(session)
            ?: error("There is no api client corresponding to session($this).")
        return apiClient as LookingTwitterApiClient
    }
}