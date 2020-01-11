package com.droibit.looking2.account.ui.twitter

import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.config.AccountConfiguration
import com.droibit.looking2.core.di.key.ViewModelKey
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [TwitterAccountListModule.BindingModule::class])
object TwitterAccountListModule {

    @Provides
    fun provideAccountListAdapter(
        fragment: TwitterAccountListFragment,
        itemPadding: ShapeAwareContentPadding
    ): TwitterAccountListAdapter {
        return TwitterAccountListAdapter(
            LayoutInflater.from(fragment.requireContext()),
            itemPadding,
            fragment::onAccountItemClick
        )
    }

    @Provides
    fun provideSignInErrorMessage(accountConfig: AccountConfiguration): LimitSignInErrorMessage {
        return LimitSignInErrorMessage(
            accountConfig.maxNumOfTwitterAccounts
        )
    }

    @Module
    interface BindingModule {

        @Binds
        @IntoMap
        @ViewModelKey(TwitterAccountListViewModel::class)
        fun bindTwitterAccountListViewModel(viewModel: TwitterAccountListViewModel): ViewModel
    }
}