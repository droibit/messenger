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

    @Component.Builder
    interface Builder {

        fun core(component: CoreComponent): Builder

        fun build(): LaunchComponent
    }

    fun inject(activity: LaunchActivity)
}

internal fun LaunchActivity.inject() {
    DaggerLaunchComponent.builder()
        .core(coreComponent())
        .build()
        .inject(this)
}