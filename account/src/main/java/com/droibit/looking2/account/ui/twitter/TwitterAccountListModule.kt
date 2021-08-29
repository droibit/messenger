package com.droibit.looking2.account.ui.twitter

import android.view.LayoutInflater
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module
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
}
