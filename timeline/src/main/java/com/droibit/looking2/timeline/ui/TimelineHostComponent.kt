package com.droibit.looking2.timeline.ui

import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.coreComponent
import com.droibit.looking2.core.di.scope.FeatureScope
import dagger.BindsInstance
import dagger.Component

@FeatureScope
@Component(
    dependencies = [CoreComponent::class],
    modules = [TimelineHostModule::class]
)
interface TimelineHostComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun activity(activity: TimelineHostActivity): Builder

        fun core(component: CoreComponent): Builder

        fun build(): TimelineHostComponent
    }

    fun inject(activity: TimelineHostActivity)
}

fun TimelineHostActivity.inject() {
    DaggerTimelineHostComponent.builder()
        .core(coreComponent())
        .activity(this)
        .build()
        .inject(this)
}
