package com.droibit.looking2.tweet.ui.input.voice

import dagger.Module
import dagger.Provides
import java.util.concurrent.TimeUnit
import javax.inject.Named

@Module
object VoiceTweetModule {

    @Named("waitDurationMillis")
    @Provides
    @JvmStatic
    fun provideWaitDurationMillis(): Long {
        return TimeUnit.SECONDS.toMillis(5)
    }
}