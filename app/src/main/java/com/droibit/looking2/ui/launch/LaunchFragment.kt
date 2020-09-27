package com.droibit.looking2.ui.launch

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.droibit.looking2.ui.Activities.Account as AccountActivity
import com.droibit.looking2.ui.Activities.Home as HomeActivity
import javax.inject.Inject

class LaunchFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LaunchViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
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
}
