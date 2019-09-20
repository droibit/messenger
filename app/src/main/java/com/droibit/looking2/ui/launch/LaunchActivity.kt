package com.droibit.looking2.ui.launch

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import javax.inject.Inject
import com.droibit.looking2.ui.Activities.Account as AccountActivity
import com.droibit.looking2.ui.Activities.Home as HomeActivity

class LaunchActivity : FragmentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LaunchViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)

        viewModel.launchDestination.observe(this) {
            val intent = when (it) {
                LaunchDestination.HOME -> HomeActivity.createIntent()
                LaunchDestination.LOGIN_TWITTER -> AccountActivity.createIntent(needTwitterSignIn = true)
            }
            startActivity(intent)
            finish()
        }
    }
}
