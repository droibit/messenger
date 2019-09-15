package com.droibit.looking2.account.ui

import android.app.Activity
import com.droibit.looking2.account.ui.signin.twitter.TwitterSignInFragment
import com.droibit.looking2.account.ui.signin.twitter.TwitterSignInModule
import com.droibit.looking2.ui.Activities.Account.EXTRA_NEED_TWITTER_SIGN_IN
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

    @Named("needTwitterSignIn")
    @Provides
    @JvmStatic
    fun provideNeedTwitterSignIn(activity: Activity): Boolean {
        val intent = requireNotNull(activity.intent)
        return intent.getBooleanExtra(EXTRA_NEED_TWITTER_SIGN_IN, false)
    }

    @Module
    interface FragmentBindingModule {

        @ContributesAndroidInjector(modules = [TwitterSignInModule::class])
        fun contributeTwitterLoginFragmentInjector(): TwitterSignInFragment
    }
}