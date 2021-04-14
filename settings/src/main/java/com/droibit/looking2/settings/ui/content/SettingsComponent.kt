package com.droibit.looking2.settings.ui.content

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.scope.FeatureScope
import com.droibit.looking2.coreComponent
import dagger.Component

@FeatureScope
@Component(dependencies = [CoreComponent::class])
interface SettingsComponent {

    @Component.Factory
    interface Factory {

        fun create(coreComponent: CoreComponent): SettingsComponent
    }

    fun inject(fragment: SettingsFragment)
}

fun SettingsFragment.inject() {
    DaggerSettingsComponent.factory()
        .create(context.coreComponent())
        .inject(this)
}
