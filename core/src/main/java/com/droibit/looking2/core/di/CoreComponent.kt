package com.droibit.looking2.core.di

import android.app.Application
import android.content.Context
import androidx.annotation.UiThread
import com.droibit.looking2.core.config.AppVersion
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
interface CoreComponent : CoreModule.Provider, RepositoryModule.Provider {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance application: Application,
            @Named("debuggable") @BindsInstance debuggable: Boolean,
            @BindsInstance appVersion: AppVersion
        ): CoreComponent
    }
}

// TOOD: Delete completely migrate to feature module
interface CoreComponentProvider {
    fun coreComponent(): CoreComponent
}

@UiThread
fun Context.coreComponent() = (this.applicationContext as CoreComponentProvider).coreComponent()
