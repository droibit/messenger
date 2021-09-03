package com.droibit.looking2.launch.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.launch.R
import com.droibit.looking2.ui.common.navigation.DeepLinkDirections.toHome
import com.droibit.looking2.ui.common.navigation.DeepLinkDirections.toSignIn
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LaunchFragment : Fragment() {

    @Inject
    lateinit var analytics: AnalyticsHelper

    private val viewModel: LaunchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.launchDestination.observe(this) {
            val toNext = when (requireNotNull(it)) {
                LaunchDestination.HOME -> toHome()
                LaunchDestination.SIGN_IN_TWITTER -> toSignIn()
            }

            with(findNavController()) {
                val launchBackStackEntry = getBackStackEntry(R.id.launchFragment)
                if (launchBackStackEntry.id == currentBackStackEntry?.id) {
                    navigate(toNext, navOptions { popUpTo(R.id.navGraphLaunch) })
                }
            }
        }
    }
}
