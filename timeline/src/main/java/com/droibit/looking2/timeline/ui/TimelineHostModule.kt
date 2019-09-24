package com.droibit.looking2.timeline.ui

import androidx.lifecycle.ViewModelProvider
import com.droibit.looking2.core.util.lifecycle.DaggerViewModelFactory
import com.droibit.looking2.timeline.ui.content.TimelineFragment
import com.droibit.looking2.timeline.ui.content.TimelineModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Module(
    includes = [
        AndroidSupportInjectionModule::class,
        TimelineHostModule.FragmentBindingModule::class,
        TimelineHostModule.BindingModule::class
    ]
)
object TimelineHostModule {

    @Module
    interface FragmentBindingModule {

        @ContributesAndroidInjector(modules = [TimelineModule::class])
        fun contributeTimelineFragmentInjector(): TimelineFragment
    }

    @Module
    interface BindingModule {

        @Binds
        fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
    }
}