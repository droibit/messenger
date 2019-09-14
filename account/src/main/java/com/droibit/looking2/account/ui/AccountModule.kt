package com.droibit.looking2.account.ui

import android.app.Activity
import com.droibit.looking2.account.ui.signin.twitter.TwitterSignInFragment
import com.droibit.looking2.account.ui.signin.twitter.TwitterSignInModule
import com.droibit.looking2.ui.Activities
import com.droibit.looking2.ui.Activities.Account.EXTRA_SIGN_IN_TWITTER
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Named

@Module(includes = [
    AndroidSupportInjectionModule::class,
    AccountModule.FragmentBindingModule::class
])
object AccountModule {

    @Provides
    @JvmStatic
    fun provideActivity(activity: AccountActivity): Activity = activity

    @Named("signInTwitterOnly")
    @Provides
    @JvmStatic
    fun provideSignInTwitterOnly(activity: Activity): Boolean {
        val intent = requireNotNull(activity.intent)
        return intent.getBooleanExtra(EXTRA_SIGN_IN_TWITTER, false)
    }

    @Module
    interface FragmentBindingModule {

        @ContributesAndroidInjector(modules = [TwitterSignInModule::class])
        fun contributeTwitterLoginFragmentInjector(): TwitterSignInFragment
    }
}