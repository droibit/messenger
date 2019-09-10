package com.droibit.looking2.ui.launch

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.droibit.looking2.R
import com.droibit.looking2.ui.Activities

class LaunchActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Activities.Account.createIntent(loginTwitter = false)
        startActivity(intent)
    }
}
