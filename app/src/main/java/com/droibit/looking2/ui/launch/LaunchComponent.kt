package com.droibit.looking2.ui.launch

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.scope.FeatureScope
import com.droibit.looking2.coreComponent
import dagger.Component

@FeatureScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [LaunchModule::class]
)
internal interface LaunchComponent {

    @Component.Factory
    interface Factory {

        fun create(component: CoreComponent): LaunchComponent
    }

    fun inject(fragment: LaunchFragment)
}

internal fun LaunchFragment.inject() {
    DaggerLaunchComponent.factory()
        .create(requireContext().coreComponent())
        .inject(this)
}
