package com.droibit.looking2.tweet.ui

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.scope.FeatureScope
import com.droibit.looking2.coreComponent
import dagger.BindsInstance
import dagger.Component

@FeatureScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [TweetModule::class]
)
interface TweetComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun activity(activity: TweetActivity): Builder

        fun core(component: CoreComponent): Builder

        fun build(): TweetComponent
    }

    fun inject(activity: TweetActivity)
}

fun TweetActivity.inject() {
    DaggerTweetComponent.builder()
        .activity(this)
        .core(coreComponent())
        .build()
        .inject(this)
}