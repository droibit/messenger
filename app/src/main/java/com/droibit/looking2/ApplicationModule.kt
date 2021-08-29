package com.droibit.looking2

import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration as WorkConfiguration
import com.droibit.looking2.core.config.AppVersion
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import timber.log.Timber

@InstallIn(SingletonComponent::class)
@Module
object ApplicationModule {

    @Named("debuggable")
    @Provides
    fun provideDebuggable() = BuildConfig.DEBUG

    @Provides
    fun provideAppVersion() = AppVersion(
        name = BuildConfig.VERSION_NAME,
        code = BuildConfig.VERSION_CODE
    )

    @Provides
    fun provideTimberTree(@Named("debuggable") debug: Boolean): Timber.Tree =
        if (debug) Timber.DebugTree() else {
            object : Timber.Tree() {
                override fun log(
                    priority: Int,
                    tag: String?,
                    message: String,
                    t: Throwable?
                ) = Unit
            }
        }

    @Provides
    fun provideWorkConfiguration(
        @Named("debuggable") debuggable: Boolean,
        hiltWorkerFactory: HiltWorkerFactory
    ): WorkConfiguration {
        // Disable log when release build.
        val loggingLevel = if (debuggable) Log.INFO else Log.ASSERT + 1
        return WorkConfiguration.Builder()
            .setMinimumLoggingLevel(loggingLevel)
            .setWorkerFactory(hiltWorkerFactory)
            .build()
    }
}
