package com.droibit.looking2.account.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.droibit.looking2.account.R
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.core.util.analytics.sendScreenView
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject
import javax.inject.Named

class AccountHostActivity : FragmentActivity(R.layout.activity_account_host),
    HasAndroidInjector,
    NavController.OnDestinationChangedListener {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var analytics: AnalyticsHelper

    @field:[Inject Named("needTwitterSignIn")]
    @JvmField
    var needTwitterSignIn: Boolean = false

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)

        // ref https://stackoverflow.com/questions/59275009/fragmentcontainerview-using-findnavcontroller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.accountNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navInflater = navController.navInflater
        navController.graph = navInflater.inflate(R.navigation.nav_graph_account)
            .apply {
                this.startDestination = if (needTwitterSignIn) {
                    R.id.navigationTwitterSignIn
                } else {
                    R.id.navigationTwitterAccountList
                }
            }
        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        analytics.sendScreenView(destination, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val navHostFragment =
            supportFragmentManager.findFragmentByTag(getString(R.string.nav_host_fragment_tag_account))
        requireNotNull(navHostFragment).childFragmentManager.fragments.firstOrNull()
            ?.onActivityResult(requestCode, resultCode, data)
    }
}
