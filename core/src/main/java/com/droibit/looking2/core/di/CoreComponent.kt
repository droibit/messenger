package com.droibit.looking2.core.di

import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [CoreModule::class])
interface CoreComponent : CoreModule.Provider {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun debuggable(@Named("debuggable") debuggable: Boolean): Builder

        fun build(): CoreComponent
    }
}