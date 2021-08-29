package com.droibit.looking2.core.di

import android.content.Context
import androidx.work.WorkManager
import com.droibit.looking2.core.config.AccountConfiguration
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.core.util.analytics.FirebaseAnalyticsHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoreModule {

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
    }
}
