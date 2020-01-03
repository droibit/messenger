package com.droibit.looking2.account.ui.list

import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.config.AccountConfiguration
import com.droibit.looking2.core.di.key.ViewModelKey
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [AccountListModule.BindingModule::class])
object AccountListModule {

    @Provides
    fun provideAccountListAdapter(
        fragment: AccountListFragment,
        itemPadding: ShapeAwareContentPadding
    ): AccountListAdapter {
        return AccountListAdapter(
            LayoutInflater.from(fragment.requireContext()),
            itemPadding,
            fragment::onAccountItemClick
        )
    }

    @Provides
    fun provideSignInErrorMessage(accountConfig: AccountConfiguration): SignInErrorMessage {
        return SignInErrorMessage(accountConfig.maxNumOfTwitterAccounts)
    }

    @Module
    interface BindingModule {

        @Binds
        @IntoMap
        @ViewModelKey(AccountListViewModel::class)
        fun bindAccountListViewModel(viewModel: AccountListViewModel): ViewModel
    }
}