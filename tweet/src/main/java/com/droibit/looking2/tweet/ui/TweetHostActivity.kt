package com.droibit.looking2.tweet.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.core.util.analytics.sendScreenView
import com.droibit.looking2.tweet.R
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class TweetHostActivity : FragmentActivity(R.layout.activity_tweet_host),
    HasAndroidInjector,
    NavController.OnDestinationChangedListener {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var analytics: AnalyticsHelper

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
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