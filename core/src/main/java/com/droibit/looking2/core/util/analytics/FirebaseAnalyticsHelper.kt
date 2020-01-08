package com.droibit.looking2.core.util.analytics

import android.app.Activity
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

class FirebaseAnalyticsHelper(
    private val analytics: FirebaseAnalytics,
    private val activity: Activity
) : AnalyticsHelper {

    constructor(activity: Activity) : this(
        FirebaseAnalytics.getInstance(activity),
        activity
    )

    override fun sendScreenView(screenName: CharSequence) {
        Timber.d("#sendScreenView: $screenName")
        analytics.setCurrentScreen(activity, screenName.toString(), null)
    }
}