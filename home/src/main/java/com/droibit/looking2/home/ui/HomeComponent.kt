package com.droibit.looking2.home.ui

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.coreComponent
import dagger.BindsInstance
import dagger.Component
import dagger.hilt.android.scopes.FragmentScoped

@Deprecated("Migrate to dagger hilt.")
@FragmentScoped
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

@Deprecated("Migrate to dagger hilt.")
fun HomeFragment.inject() {
    DaggerHomeComponent.factory()
        .create(this, requireContext().coreComponent())
        .inject(this)
}
