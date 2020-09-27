package com.droibit.looking2.account.ui

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.scope.FeatureScope
import com.droibit.looking2.coreComponent
import dagger.BindsInstance
import dagger.Component

@FeatureScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [AccountHostModule::class]
)
interface AccountComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun activity(activity: AccountHostActivity): Builder

        fun core(component: CoreComponent): Builder

        fun build(): AccountComponent
    }

    fun inject(activity: AccountHostActivity)
}

fun AccountHostActivity.inject() {
    DaggerAccountComponent.builder()
        .core(coreComponent())
        .activity(this)
        .build()
        .inject(this)
}
