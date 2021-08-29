package com.droibit.looking2.tweet.ui

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.coreComponent
import dagger.BindsInstance
import dagger.Component
import dagger.hilt.android.scopes.ActivityScoped

@Deprecated("Migrate to dagger hilt.")
@ActivityScoped
@Component(
    dependencies = [CoreComponent::class],
    modules = [TweetHostModule::class]
)
interface TweetComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun activity(activity: TweetHostActivity): Builder

        fun core(component: CoreComponent): Builder

        fun build(): TweetComponent
    }

    fun inject(activity: TweetHostActivity)
}

@Deprecated("Migrate to dagger hilt.")
fun TweetHostActivity.inject() {
    DaggerTweetComponent.builder()
        .activity(this)
        .core(coreComponent())
        .build()
        .inject(this)
}
