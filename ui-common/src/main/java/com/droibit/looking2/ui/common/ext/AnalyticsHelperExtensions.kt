package com.droibit.looking2.ui.common.ext

import android.app.Activity
import androidx.annotation.UiThread
import androidx.navigation.NavDestination
import com.droibit.looking2.core.util.analytics.AnalyticsHelper

@UiThread
fun AnalyticsHelper.sendScreenView(destination: NavDestination, screenClass: Activity) {
    val label = destination.label?.takeIf { it.isNotBlank() } ?: return
    sendScreenView(label, screenClass)
}
