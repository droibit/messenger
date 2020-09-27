package com.droibit.looking2.home.ui

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.scope.FeatureScope
import com.droibit.looking2.coreComponent
import dagger.BindsInstance
import dagger.Component

@FeatureScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [HomeModule::class]
)
interface HomeComponent {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance fragment: HomeFragment,
            component: CoreComponent
        ): HomeComponent
    }

    fun inject(fragment: HomeFragment)
}

fun HomeFragment.inject() {
    DaggerHomeComponent.factory()
        .create(this, requireContext().coreComponent())
        .inject(this)
}
