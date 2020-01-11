package com.droibit.looking2.core.di

import android.app.Application
import android.content.Context
import androidx.work.WorkManager
import com.droibit.looking2.core.config.AccountConfiguration
import com.droibit.looking2.core.config.AppVersion
import com.droibit.looking2.core.data.TwitterBootstrap
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.core.util.analytics.FirebaseAnalyticsHelper
import com.droibit.looking2.core.util.checker.PlayServicesChecker
import com.google.android.gms.common.GoogleApiAvailability
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [CoreModule.BindingModule::class])
object CoreModule {

    @Named("appContext")
    @Provides
    fun provideContext(application: Application): Context = application

    @Provides
    fun provideGoogleApiAvailability(): GoogleApiAvailability = GoogleApiAvailability.getInstance()

    @Provides
    fun provideWorkManager(@Named("appContext") context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Singleton
    @Provides
    fun provideAccountConfiguration(): AccountConfiguration {
        return AccountConfiguration(
            maxNumOfTwitterAccounts = 2
        )
    }

    @Module
    interface BindingModule {

        @Binds
        fun bindAnalyticsHelper(analytics: FirebaseAnalyticsHelper): AnalyticsHelper
    }

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

        fun provideAccountConfiguration(): AccountConfiguration

        fun provideAnalytics(): AnalyticsHelper
    }
}