package com.droibit.looking2.account.ui

import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import com.droibit.looking2.account.ui.list.AccountListFragment
import com.droibit.looking2.account.ui.list.AccountListModule
import com.droibit.looking2.account.ui.signin.twitter.TwitterSignInFragment
import com.droibit.looking2.account.ui.signin.twitter.TwitterSignInModule
import com.droibit.looking2.core.di.scope.FeatureScope
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.core.util.lifecycle.DaggerViewModelFactory
import com.droibit.looking2.ui.Activities.Account.EXTRA_NEED_TWITTER_SIGN_IN
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Named

@Module(
    includes = [
        AndroidSupportInjectionModule::class,
        AccountModule.FragmentBindingModule::class,
        AccountModule.BindingModule::class
    ]
)
object AccountModule {

    @Provides
    fun provideActivity(activity: AccountActivity): Activity = activity

    @Named("needTwitterSignIn")
    @Provides
    fun provideNeedTwitterSignIn(activity: Activity): Boolean {
        val intent = requireNotNull(activity.intent)
        return intent.getBooleanExtra(EXTRA_NEED_TWITTER_SIGN_IN, false)
    }

    @FeatureScope
    @Provides
    fun provideContentPadding(activity: AccountActivity): ShapeAwareContentPadding {
        return ShapeAwareContentPadding(activity)
    }

    @Module
    interface FragmentBindingModule {

        @ContributesAndroidInjector(modules = [TwitterSignInModule::class])
        fun contributeTwitterLoginFragmentInjector(): TwitterSignInFragment

        @ContributesAndroidInjector(modules = [AccountListModule::class])
        fun contributeAccountListFragmentInjector(): AccountListFragment
    }

    @Module
    interface BindingModule {

        @Binds
        fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
    }
}