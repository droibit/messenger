package com.droibit.looking2.core.data

import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterConfig
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TwitterBootstrap @Inject constructor(
    private val twitterConfig: TwitterConfig,
    private val repository: dagger.Lazy<AccountRepository>
) {

    fun initialize(): Unit = runBlocking {
        Twitter.initialize(twitterConfig)
        repository.get().initialize()
    }
}