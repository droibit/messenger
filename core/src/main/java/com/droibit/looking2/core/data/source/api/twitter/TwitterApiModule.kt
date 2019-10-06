package com.droibit.looking2.core.data.source.api.twitter

import android.content.Context
import android.util.Log
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Logger
import com.twitter.sdk.android.core.SessionManager
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.internal.TwitterApi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
object TwitterApiModule {

    @Singleton
    @Provides
    @JvmStatic
    fun provideTwitterApi(): TwitterApi = TwitterApi()

    @Provides
    @JvmStatic
    fun provideTwitterConfig(
        @Named("appContext") context: Context,
        logger: Logger,
        @Named("debuggable") debug: Boolean
    ): TwitterConfig =
        TwitterConfig.Builder(context)
            .logger(logger)
            .debug(debug)
            .build()

    @Provides
    @JvmStatic
    fun provideLogger(@Named("debuggable") debug: Boolean): Logger =
        DefaultLogger(if (debug) Int.MAX_VALUE else Log.VERBOSE)

    @Provides
    @JvmStatic
    fun provideTwitterCore(): TwitterCore = TwitterCore.getInstance()

    @Provides
    @JvmStatic
    fun provideSessionManager(twitterCore: TwitterCore): SessionManager<TwitterSession> =
        twitterCore.sessionManager

    @Singleton
    @Provides
    @JvmStatic
    fun provideAppTwitterApiClientFactory(
        okHttpClient: OkHttpClient
    ): AppTwitterApiClient.Factory {
        return object : AppTwitterApiClient.Factory {
            override fun get(session: TwitterSession): AppTwitterApiClient {
                return AppTwitterApiClient(session, okHttpClient)
            }
        }
    }
}