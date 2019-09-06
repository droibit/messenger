package com.droibit.looking2.core.di

import android.app.Application
import com.droibit.looking2.core.data.repository.RepositoryModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoreModule::class,
        RepositoryModule::class
    ]
)
interface CoreComponent : CoreModule.Provider {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun debuggable(@Named("debuggable") debuggable: Boolean): Builder

        fun build(): CoreComponent
    }
}