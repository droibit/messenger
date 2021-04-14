package com.droibit.looking2.core.data.source.remote.twitter.api

import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession

class AppTwitterApiClientFactoryDelegate(
    private val twitterCore: TwitterCore
) : AppTwitterApiClient.Factory {
    override fun get(session: TwitterSession): AppTwitterApiClient {
        val apiClient = twitterCore.getApiClient(session)
            ?: error("There is no api client corresponding to session($this).")
        return apiClient as AppTwitterApiClient
    }
}
