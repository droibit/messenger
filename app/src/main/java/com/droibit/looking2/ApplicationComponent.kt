package com.droibit.looking2

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.scope.ApplicationScope
import dagger.Component

@Deprecated("Migrate to dagger hilt.")
@ApplicationScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [ApplicationModule::class]
)
interface ApplicationComponent {

    @Deprecated("Migrate to dagger hilt.")
    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): ApplicationComponent
    }

    fun inject(application: LookingApplication)
}
