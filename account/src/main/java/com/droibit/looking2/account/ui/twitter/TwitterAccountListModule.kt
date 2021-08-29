package com.droibit.looking2.account.ui.twitter

import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.di.key.ViewModelKey
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.multibindings.IntoMap

@InstallIn(FragmentComponent::class)
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

    @Deprecated("Migrate to dagger hilt.")
    @Module
    interface BindingModule {

        @Binds
        @IntoMap
        @ViewModelKey(TwitterAccountListViewModel::class)
        fun bindTwitterAccountListViewModel(viewModel: TwitterAccountListViewModel): ViewModel
    }
}
