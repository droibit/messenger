package com.droibit.looking2.timeline.ui

import androidx.lifecycle.ViewModelProvider
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.core.util.lifecycle.DaggerViewModelFactory
import com.droibit.looking2.timeline.ui.content.TimelineFragment
import com.droibit.looking2.timeline.ui.content.TimelineModule
import com.droibit.looking2.timeline.ui.content.mylist.MyListsFragment
import com.droibit.looking2.timeline.ui.content.mylist.MyListsModule
import com.droibit.looking2.timeline.ui.content.photo.PhotoFragment
import com.droibit.looking2.timeline.ui.content.photo.PhotoModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.hilt.android.scopes.ActivityScoped

@Module(
    includes = [
        AndroidSupportInjectionModule::class,
        TimelineHostModule.FragmentBindingModule::class,
        TimelineHostModule.BindingModule::class
    ]
)
object TimelineHostModule {

    @ActivityScoped
    @Provides
    fun provideContentPadding(activity: TimelineHostActivity): ShapeAwareContentPadding {
        return ShapeAwareContentPadding(activity)
    }

    @Deprecated("Migrate to dagger hilt.")
    @Module
    interface FragmentBindingModule {

        @ContributesAndroidInjector(modules = [TimelineModule::class])
        fun contributeTimelineFragmentInjector(): TimelineFragment

        @ContributesAndroidInjector(modules = [PhotoModule::class])
        fun contributePhotoFragmentInjector(): PhotoFragment

        @ContributesAndroidInjector(modules = [MyListsModule::class])
        fun contributeMyListsFragmentInjector(): MyListsFragment
    }

    @Deprecated("Migrate to dagger hilt.")
    @Module
    interface BindingModule {

        @Binds
        fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
    }
}
