package com.droibit.looking2

import dagger.Module
import dagger.Provides
import timber.log.Timber
import javax.inject.Named

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
}