package com.droibit.looking2.settings.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.droibit.looking2.settings.ui.content.SettingsFragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class SettingsHostActivity : ComponentActivity(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commitNow()
        }
    }
}
