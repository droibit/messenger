package com.droibit.looking2.settings.ui.content

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.coreComponent
import dagger.Component
import dagger.hilt.android.scopes.FragmentScoped

@Deprecated("Migrate to dagger hilt.")
@FragmentScoped
@Component(dependencies = [CoreComponent::class])
interface SettingsComponent {

    @Component.Factory
    interface Factory {

        fun create(coreComponent: CoreComponent): SettingsComponent
    }

    fun inject(fragment: SettingsFragment)
}

@Deprecated("Migrate to dagger hilt.")
fun SettingsFragment.inject() {
    DaggerSettingsComponent.factory()
        .create(context.coreComponent())
        .inject(this)
}
