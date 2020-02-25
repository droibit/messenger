package com.droibit.looking2.ui.launch

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.droibit.looking2.ui.Activities
import javax.inject.Inject

class LaunchFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LaunchViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("FragmentLiveDataObserve")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.launchDestination.observe(this) {
            val intent = when (it) {
                LaunchDestination.HOME -> Activities.Home.createIntent()
                LaunchDestination.LOGIN_TWITTER -> Activities.Account.createIntent(needTwitterSignIn = true)
            }
            startActivity(intent)
            requireActivity().finish()
        }
    }
}