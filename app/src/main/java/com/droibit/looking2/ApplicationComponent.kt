package com.droibit.looking2

import android.app.Application
import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.scope.ApplicationScope
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(dependencies = [CoreComponent::class], modules = [ApplicationModule::class])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun core(component: CoreComponent): Builder

        fun build(): ApplicationComponent
    }

    fun inject(application: LookingApplication)
}