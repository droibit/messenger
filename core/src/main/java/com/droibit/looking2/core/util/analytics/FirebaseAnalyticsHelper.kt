package com.droibit.looking2.core.util.analytics

import android.app.Activity
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsHelper @Inject constructor(
    private val analytics: FirebaseAnalytics
) : AnalyticsHelper {

    override fun sendScreenView(screenName: CharSequence, activity: Activity) {
        Timber.d("#sendScreenView: $screenName")
        analytics.setCurrentScreen(activity, screenName.toString(), null)
    }
}