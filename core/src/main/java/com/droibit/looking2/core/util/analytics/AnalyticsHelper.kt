package com.droibit.looking2.core.util.analytics

import android.app.Activity
import androidx.annotation.UiThread
import androidx.navigation.NavDestination

interface AnalyticsHelper {

    @UiThread
    fun sendScreenView(screenName: CharSequence, screenClass: Activity?)

    fun setNumOfTwitterAccounts(value: Int)

    fun setNumOfGetTweets(value: Int)
}

@UiThread
fun AnalyticsHelper.sendScreenView(destination: NavDestination, screenClass: Activity) {
    val label = destination.label?.takeIf { it.isNotBlank() } ?: return
    sendScreenView(label, screenClass)
}
