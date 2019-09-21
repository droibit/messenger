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

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun activity(activity: HomeActivity): Builder

        fun core(component: CoreComponent): Builder

        fun build(): HomeComponent
    }

    fun inject(activity: HomeActivity)
}

fun HomeActivity.inject() {
    DaggerHomeComponent.builder()
        .core(coreComponent())
        .activity(this)
        .build()
        .inject(this)
}