package com.droibit.looking2.account.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.droibit.looking2.account.R

class AccountActivity : FragmentActivity(R.layout.activity_account) {

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }
}
