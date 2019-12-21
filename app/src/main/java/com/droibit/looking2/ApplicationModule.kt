package com.droibit.looking2

import android.util.Log
import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Named
import androidx.work.Configuration as WorkConfiguration

@Module
object ApplicationModule {

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
        @Named("debuggable") debuggable: Boolean
    ): WorkConfiguration {
        // Disable log when release build.
        val loggingLevel = if (debuggable) Log.INFO else Log.ASSERT + 1
        return WorkConfiguration.Builder()
            .setMinimumLoggingLevel(loggingLevel)
            .build()
    }


}