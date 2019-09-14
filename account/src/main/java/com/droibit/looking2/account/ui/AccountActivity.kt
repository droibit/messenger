package com.droibit.looking2.account.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.droibit.looking2.account.R
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject
import javax.inject.Named

class AccountActivity : FragmentActivity(R.layout.activity_account), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @field:[Inject Named("signInTwitterOnly")]
    @JvmField
    var signInTwitterOnly: Boolean = false

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)

        val navController = findNavController(R.id.accountNavHostFragment)
        val navInflater = navController.navInflater
        navController.graph = if (signInTwitterOnly) {
            navInflater.inflate(R.navigation.nav_graph_account).apply {
                startDestination = R.id.twitterSignInFragment
            }
        } else {
            TODO()
        }
    }
}
