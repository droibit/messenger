package com.droibit.looking2.account.ui

import androidx.lifecycle.ViewModelProvider
import com.droibit.looking2.account.ui.twitter.TwitterAccountListFragment
import com.droibit.looking2.account.ui.twitter.TwitterAccountListModule
import com.droibit.looking2.account.ui.twitter.signin.TwitterSignInFragment
import com.droibit.looking2.account.ui.twitter.signin.TwitterSignInModule
import com.droibit.looking2.core.ui.Activities.Account.EXTRA_NEED_TWITTER_SIGN_IN
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.core.util.lifecycle.DaggerViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Named

@InstallIn(ActivityComponent::class)
@Module(
    includes = [
        AndroidSupportInjectionModule::class,
        AccountHostModule.FragmentBindingModule::class,
        AccountHostModule.BindingModule::class
    ]
)
object AccountHostModule {

    @Named("needTwitterSignIn")
    @Provides
    fun provideNeedTwitterSignIn(activity: AccountHostActivity): Boolean {
        val intent = requireNotNull(activity.intent)
        return intent.getBooleanExtra(EXTRA_NEED_TWITTER_SIGN_IN, false)
    }

    @ActivityScoped
    @Provides
    fun provideContentPadding(activity: AccountHostActivity): ShapeAwareContentPadding {
        return ShapeAwareContentPadding(activity)
    }

    @Deprecated("Migrate to dagger hilt.")
    @Module
    interface FragmentBindingModule {

        @ContributesAndroidInjector(modules = [TwitterSignInModule::class])
        fun contributeTwitterLoginFragmentInjector(): TwitterSignInFragment

        @ContributesAndroidInjector(modules = [TwitterAccountListModule::class])
        fun contributeAccountListFragmentInjector(): TwitterAccountListFragment
    }

    @Deprecated("Migrate to dagger hilt.")
    @Module
    interface BindingModule {

        @Binds
        fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
    }
}
