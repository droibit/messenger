package com.droibit.looking2.tweet.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.tweet.R
import com.droibit.looking2.ui.common.ext.sendScreenView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TweetHostActivity :
    FragmentActivity(R.layout.activity_tweet_host),
    NavController.OnDestinationChangedListener {

    @Inject
    lateinit var analytics: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ref https://stackoverflow.com/questions/59275009/fragmentcontainerview-using-findnavcontroller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.tweetNavHostFragment) as NavHostFragment
        navHostFragment.navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        analytics.sendScreenView(destination, this)
    }
}
