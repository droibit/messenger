package com.droibit.looking2.core.util.analytics

import android.app.Activity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Param.SCREEN_CLASS
import com.google.firebase.analytics.FirebaseAnalytics.Param.SCREEN_NAME
import com.google.firebase.analytics.ktx.logEvent
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val UPROP_NUM_OF_TWITTER_ACCOUNTS = "num_of_twitter_accounts"
private const val UPROP_NUM_OF_GET_TWEETS = "num_of_twitter_accounts"

@Singleton
class FirebaseAnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsHelper {

    override fun sendScreenView(screenName: CharSequence, screenClass: Activity?) {
        Timber.d("#sendScreenView: $screenName")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(SCREEN_NAME, screenName.toString())
            param(SCREEN_CLASS, if (screenClass == null) {
                screenName.toString()
            } else {
                screenClass.javaClass.simpleName
            })
        }
    }

    override fun setNumOfTwitterAccounts(value: Int) {
        firebaseAnalytics.setUserProperty(UPROP_NUM_OF_TWITTER_ACCOUNTS, "$value")
    }

    override fun setNumOfGetTweets(value: Int) {
        firebaseAnalytics.setUserProperty(UPROP_NUM_OF_GET_TWEETS, "$value")
    }
}
