package com.droibit.looking2.ui.launch

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.droibit.looking2.R
import com.droibit.looking2.core.ui.Activities.Account as AccountActivity
import com.droibit.looking2.core.ui.Activities.Home as HomeActivity
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
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
            val intent = when (requireNotNull(it)) {
                LaunchDestination.HOME -> HomeActivity.createIntent()
                LaunchDestination.LOGIN_TWITTER -> AccountActivity.createIntent(
                    needTwitterSignIn = true
                )
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onResume() {
        super.onResume()

        analytics.sendScreenView(
            screenName = getString(R.string.launch_nav_label),
            screenClass = null
        )
    }
}
