package com.droibit.looking2.settings.ui

import com.droibit.looking2.settings.ui.content.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Module(
    includes = [
        AndroidSupportInjectionModule::class,
        SettingsHostModule.FragmentBindingModule::class
    ]
)
object SettingsHostModule {

    @Module
    interface FragmentBindingModule {

        @ContributesAndroidInjector
        fun contributeSettingsFragmentInjector(): SettingsFragment
    }
}