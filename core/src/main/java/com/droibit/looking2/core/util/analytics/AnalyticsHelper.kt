package com.droibit.looking2.core.util.analytics

import android.app.Activity
import androidx.annotation.UiThread

interface AnalyticsHelper {

    @UiThread
    fun sendScreenView(screenName: CharSequence, screenClass: Activity?)

    fun setNumOfTwitterAccounts(value: Int)

    fun setNumOfGetTweets(value: Int)
}
