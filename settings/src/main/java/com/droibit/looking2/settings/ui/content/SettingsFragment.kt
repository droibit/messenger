package com.droibit.looking2.settings.ui.content

import android.os.Bundle
import android.preference.PreferenceFragment
import com.droibit.looking2.settings.R
import dagger.android.AndroidInjection

class SettingsFragment : PreferenceFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }
}