package com.droibit.looking2.core.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.droibit.looking2.core.config.AccountConfiguration
import com.droibit.looking2.core.config.AppVersion
import com.droibit.looking2.core.data.TwitterBootstrap
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.core.util.analytics.FirebaseAnalyticsHelper
import com.droibit.looking2.core.util.checker.PhoneDeviceTypeChecker
import com.droibit.looking2.core.util.lifecycle.DaggerViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module(includes = [CoreModule.BindingModule::class])
object CoreModule {

    @ApplicationContext
    @Provides
    @Deprecated("Migrate to dagger hilt.")
    fun provideContext(application: Application): Context = application

    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Singleton
    @Provides
    fun provideAccountConfiguration(): AccountConfiguration {
        return AccountConfiguration(
            maxNumOfTwitterAccounts = 3
        )
    }

    @Module
    @InstallIn(SingletonComponent::class)
    interface BindingModule {

        @Binds
        fun bindAnalyticsHelper(analytics: FirebaseAnalyticsHelper): AnalyticsHelper

        @Binds
        fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
    }

    @Deprecated("Migrate to dagger hilt.")
    interface Provider {

        fun provideApplication(): Application

        @ApplicationContext
        fun provideApplicationContext(): Context

        @Named("debuggable")
        fun provideDebuggable(): Boolean

        fun provideAppVersion(): AppVersion

        fun provideTwitterBootstrap(): TwitterBootstrap

        fun provideWorkManager(): WorkManager

        fun provideAccountConfiguration(): AccountConfiguration

        fun provideAnalytics(): AnalyticsHelper

        fun providePhoneDeviceTypeChecker(): PhoneDeviceTypeChecker
    }
}
