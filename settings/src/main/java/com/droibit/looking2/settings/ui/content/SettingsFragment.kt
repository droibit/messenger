package com.droibit.looking2.settings.ui.content

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.view.View
import com.droibit.looking2.core.config.AppVersion
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.settings.R
import com.github.droibit.chopstick.preference.bindPreference
import com.github.droibit.oss_licenses.ui.wearable.WearableOssLicensesActivity
import javax.inject.Inject

class SettingsFragment : PreferenceFragment() {

    @Inject
    lateinit var appVersion: AppVersion

    @Inject
    lateinit var analytics: AnalyticsHelper

    private val appVersionPref: Preference by bindPreference(R.string.pref_app_version_key)

    private val ossLicensesPref: Preference by bindPreference(R.string.pref_app_oss_key)

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appVersionPref.summary = getString(R.string.pref_app_version_summary, appVersion.name)

        ossLicensesPref.setOnPreferenceClickListener {
            startActivity(WearableOssLicensesActivity.createIntent(context))
            true
        }
    }

    override fun onResume() {
        super.onResume()
        analytics.sendScreenView(
            screenName = getString(R.string.settings_nav_label),
            screenClass = null
        )
    }
}
