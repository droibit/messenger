package com.droibit.looking2.tweet.ui.input.voice

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import java.util.concurrent.TimeUnit
import javax.inject.Named

@InstallIn(FragmentComponent::class)
@Module
object VoiceTweetModule {

    @Named("waitDurationMillis")
    @Provides
    fun provideWaitDurationMillis(): Long {
        return TimeUnit.SECONDS.toMillis(5)
    }
}
