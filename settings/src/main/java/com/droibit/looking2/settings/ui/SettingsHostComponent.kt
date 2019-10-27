package com.droibit.looking2.settings.ui

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.scope.FeatureScope
import com.droibit.looking2.coreComponent
import dagger.Component

@FeatureScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [SettingsHostModule::class]
)
interface SettingsHostComponent {

    @Component.Builder
    interface Builder {

        fun core(component: CoreComponent): Builder

        fun build(): SettingsHostComponent
    }

    fun inject(activity: SettingsHostActivity)
}

fun SettingsHostActivity.inject() {
    DaggerSettingsHostComponent.builder()
        .core(coreComponent())
        .build()
        .inject(this)
}