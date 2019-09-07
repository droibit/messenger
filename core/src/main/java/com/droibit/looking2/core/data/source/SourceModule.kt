package com.droibit.looking2.core.data.source

import com.droibit.looking2.core.data.source.api.WearOAuthModule
import com.droibit.looking2.core.data.source.api.twitter.TwitterModule
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.HEADERS
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [
    TwitterModule::class,
    WearOAuthModule::class
])
object SourceModule {

    @Named("httpLogging")
    @Singleton
    @Provides
    @JvmStatic
    fun provideHttpLoggingInterceptor(@Named("debuggable") debug: Boolean): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = if (debug) HEADERS else NONE
        }
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideOkHttp(
        @Named("httpLogging") httpLoggingInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

}