package com.droibit.looking2.timeline.ui

import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@InstallIn(ActivityComponent::class)
@Module
object TimelineHostModule {

    @ActivityScoped
    @Provides
    fun provideContentPadding(activity: TimelineHostActivity): ShapeAwareContentPadding {
        return ShapeAwareContentPadding(activity)
    }
}
