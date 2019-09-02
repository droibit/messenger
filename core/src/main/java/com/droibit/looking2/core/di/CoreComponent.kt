package com.droibit.looking2.core.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [CoreModule::class])
interface CoreComponent {

    @Component.Builder
    interface Builder {
        // fun coreModule(module: CoreModule): Builder

        fun build(): CoreComponent
    }
}