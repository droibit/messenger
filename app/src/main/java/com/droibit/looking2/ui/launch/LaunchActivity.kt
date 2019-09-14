package com.droibit.looking2.ui.launch

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.droibit.looking2.R
import com.droibit.looking2.ui.Activities
import javax.inject.Inject

class LaunchActivity : FragmentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: LaunchViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.launchDestination.observe(this) {
            val intent = Activities.Account.createIntent(signInTwitter = false)
            startActivity(intent)
            finish()
        }
    }
}
