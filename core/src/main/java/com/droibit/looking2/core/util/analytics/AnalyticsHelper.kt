package com.droibit.looking2.core.util.analytics

import androidx.annotation.UiThread
import androidx.navigation.NavDestination

interface AnalyticsHelper {

    @UiThread
    fun sendScreenView(screenName: CharSequence)
}

@UiThread
fun AnalyticsHelper.sendScreenView(destination: NavDestination) {
    val label = destination.label?.takeIf { it.isNotEmpty() } ?: return
    sendScreenView(label)
}