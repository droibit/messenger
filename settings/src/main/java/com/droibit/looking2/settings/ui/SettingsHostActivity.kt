package com.droibit.looking2.settings.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.droibit.looking2.settings.ui.content.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsHostActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commitNow()
        }
    }
}
