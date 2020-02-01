package com.droibit.looking2.core.data.source.api

import com.droibit.looking2.core.data.source.api.twitter.TwitterApiModule
import com.droibit.looking2.core.util.Stetho
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS
import java.util.Optional
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(
    includes = [
        TwitterApiModule::class,
        WearOAuthModule::class
    ]
)
object ApiModule {

    @Named("httpLogging")
    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(@Named("debuggable") debug: Boolean): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = if (debug) HEADERS else HttpLoggingInterceptor.Level.NONE
        }
    }

    @Named("stetho")
    @Singleton
    @Provides
    fun provideStethoInterceptor(): Optional<Interceptor> {
        return Stetho.interceptor()
    }

    @Singleton
    @Provides
    fun provideOkHttp(
        @Named("httpLogging") httpLoggingInterceptor: Interceptor,
        @Named("stetho") stethoInterceptor: Optional<Interceptor>

    ): OkHttpClient {
        return OkHttpClient.Builder()
            .apply {
                stethoInterceptor.ifPresent {
                    addNetworkInterceptor(it)
                }
            }
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }
}