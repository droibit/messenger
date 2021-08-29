package com.droibit.looking2.ui.launch

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.coreComponent
import dagger.Component
import dagger.hilt.android.scopes.FragmentScoped

@Deprecated("Migrate to dagger hilt.")
@FragmentScoped
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

@Deprecated("Migrate to dagger hilt.")
internal fun LaunchFragment.inject() {
    DaggerLaunchComponent.factory()
        .create(requireContext().coreComponent())
        .inject(this)
}
