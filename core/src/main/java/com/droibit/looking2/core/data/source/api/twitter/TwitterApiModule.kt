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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Named
import javax.inject.Singleton

@Module
object TwitterApiModule {

    @Singleton
    @Provides
    fun provideTwitterApi(): TwitterApi = TwitterApi()

    @Provides
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
    fun provideLogger(@Named("debuggable") debug: Boolean): Logger =
        DefaultLogger(if (debug) Int.MAX_VALUE else Log.VERBOSE)

    @Provides
    fun provideTwitterCore(): TwitterCore = TwitterCore.getInstance()

    @Provides
    fun provideSessionManager(twitterCore: TwitterCore): SessionManager<TwitterSession> =
        twitterCore.sessionManager

    @Singleton
    @Provides
    fun provideAppTwitterApiClientFactory(
        okHttpClient: OkHttpClient
    ): AppTwitterApiClient.Factory {
        return object : AppTwitterApiClient.Factory {
            override fun get(session: TwitterSession): AppTwitterApiClient {
                return AppTwitterApiClient(session, okHttpClient)
            }
        }
    }

    @Named("twitterApi")
    @Provides
    fun provideTwitterApiDateFormat(): DateFormat {
        return SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH)
    }
}