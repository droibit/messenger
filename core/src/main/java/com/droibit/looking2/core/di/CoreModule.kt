package com.droibit.looking2.core.di

import dagger.Module
import javax.inject.Named

@Module
object CoreModule {

    interface Provider {

        @Named("debuggable")
        fun provideDebuggable(): Boolean
    }
}