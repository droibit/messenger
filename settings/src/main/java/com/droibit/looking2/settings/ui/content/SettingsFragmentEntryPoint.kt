package com.droibit.looking2.settings.ui.content

import com.droibit.looking2.core.config.AppVersion
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface SettingsFragmentEntryPoint {
    val appVersion: AppVersion
    val analytics: AnalyticsHelper
}
