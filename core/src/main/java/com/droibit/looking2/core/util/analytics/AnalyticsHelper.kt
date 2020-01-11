package com.droibit.looking2.core.util.analytics

import android.app.Activity
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import androidx.navigation.NavDestination

interface AnalyticsHelper {

    @UiThread
    fun sendScreenView(screenName: CharSequence, activity: Activity)
}

@UiThread
fun AnalyticsHelper.sendScreenView(destination: NavDestination, activity: Activity) {
    val label = destination.label?.takeIf { it.isNotEmpty() } ?: return
    sendScreenView(label, activity)
}

@UiThread
fun AnalyticsHelper.sendScreenView(@StringRes screenName: Int, activity: Activity) {
    sendScreenView(activity.getString(screenName), activity)
}