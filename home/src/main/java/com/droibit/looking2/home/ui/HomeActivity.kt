package com.droibit.looking2.home.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.droibit.looking2.home.R
import com.droibit.looking2.home.databinding.ActivityHomeBinding

class HomeActivity : FragmentActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
    }
}
