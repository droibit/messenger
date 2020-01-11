package com.droibit.looking2.core.util.analytics

import android.app.Activity
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private const val UPROP_NUM_OF_TWITTER_ACCOUNTS = "num_of_twitter_accounts"
private const val UPROP_NUM_OF_GET_TWEETS = "num_of_twitter_accounts"

@Singleton
class FirebaseAnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : AnalyticsHelper {

    override fun sendScreenView(screenName: CharSequence, activity: Activity) {
        Timber.d("#sendScreenView: $screenName")
        firebaseAnalytics.setCurrentScreen(activity, screenName.toString(), null)
    }

    override fun setNumOfTwitterAccounts(value: Int) {
        firebaseAnalytics.setUserProperty(UPROP_NUM_OF_TWITTER_ACCOUNTS, "$value")
    }

    override fun setNumOfGetTweets(value: Int) {
        firebaseAnalytics.setUserProperty(UPROP_NUM_OF_GET_TWEETS, "$value")
    }
}