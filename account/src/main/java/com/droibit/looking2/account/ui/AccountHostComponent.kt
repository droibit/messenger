package com.droibit.looking2.account.ui

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.coreComponent
import dagger.BindsInstance
import dagger.Component
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
@Component(
    dependencies = [CoreComponent::class],
    modules = [AccountHostModule::class]
)
@Deprecated("Migrate to dagger hilt.")
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

@Deprecated("Migrate to dagger hilt.")
fun AccountHostActivity.inject() {
    DaggerAccountComponent.builder()
        .core(coreComponent())
        .activity(this)
        .build()
        .inject(this)
}
