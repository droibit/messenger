package com.droibit.looking2.core.di

import android.app.Application
import android.content.Context
import androidx.work.WorkManager
import com.droibit.looking2.core.config.AppVersion
import com.droibit.looking2.core.data.TwitterBootstrap
import com.droibit.looking2.core.util.checker.PlayServicesChecker
import com.google.android.gms.common.GoogleApiAvailability
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
object CoreModule {

    @Named("appContext")
    @Provides
    fun provideContext(application: Application): Context = application

    @Provides
    fun provideGoogleApiAvailability(): GoogleApiAvailability = GoogleApiAvailability.getInstance()

    @Singleton
    @Provides
    fun provideWorkManager(@Named("appContext") context: Context): WorkManager =
        WorkManager.getInstance(context)

    interface Provider {

        fun provideApplication(): Application

        @Named("appContext")
        fun provideApplicationContext(): Context

        @Named("debuggable")
        fun provideDebuggable(): Boolean

        fun provideAppVersion(): AppVersion

        fun providePlayServicesChecker(): PlayServicesChecker

        fun provideTwitterBootstrap(): TwitterBootstrap

        fun provideWorkManager(): WorkManager
    }
}