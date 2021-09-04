package com.droibit.looking2.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.droibit.looking2.R
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.core.util.analytics.AnalyticsHelper.Companion.KEY_OVERRIDE_LABEL
import com.droibit.looking2.ui.common.ext.sendScreenView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import timber.log.Timber

@AndroidEntryPoint
class MainActivity :
    FragmentActivity(R.layout.activity_main),
    NavController.OnDestinationChangedListener {

    @Inject
    lateinit var analytics: AnalyticsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val overrideLabel = arguments?.getString(KEY_OVERRIDE_LABEL)
        if (overrideLabel != null) {
            destination.label = overrideLabel
        }
        Timber.d("#onDestinationChanged: $destination, args=$arguments")

        analytics.sendScreenView(destination, this)
    }
}
