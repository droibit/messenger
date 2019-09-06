package com.droibit.looking2.core.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object CoreModule {

    @JvmStatic
    @Named("appContext")
    @Provides
    fun provideContext(application: Application): Context = application

    interface Provider {

        fun provideApplication(): Application

        @Named("appContext")
        fun provideApplicationContext(): Context

        @Named("debuggable")
        fun provideDebuggable(): Boolean
    }
}