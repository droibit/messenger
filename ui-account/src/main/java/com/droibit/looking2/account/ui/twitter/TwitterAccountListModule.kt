package com.droibit.looking2.account.ui.twitter

import androidx.fragment.app.Fragment
import com.droibit.looking2.ui.common.view.ShapeAwareContentPadding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module
object TwitterAccountListModule {

    @Provides
    fun provideOnTwitterAccountItemClickListener(fragment: Fragment) =
        fragment as TwitterAccountListAdapter.OnItemClickListener

    @Provides
    fun provideOnTwitterAccountFooterClickListener(fragment: Fragment) =
        fragment as TwitterAccountListFooterAdapter.OnItemClickListener

    @Provides
    fun provideAccountListAdapter(
        itemPadding: ShapeAwareContentPadding,
        itemClickListener: TwitterAccountListAdapter.OnItemClickListener
    ) = TwitterAccountListAdapter(
        itemPadding,
        itemClickListener
    )

    @Provides
    fun provideAccountListHeaderAdapter(
        itemPadding: ShapeAwareContentPadding,
    ) = TwitterAccountListHeaderAdapter(itemPadding)

    @Provides
    fun provideAccountListFooterAdapter(
        itemPadding: ShapeAwareContentPadding,
        itemClickListener: TwitterAccountListFooterAdapter.OnItemClickListener
    ) = TwitterAccountListFooterAdapter(
        itemPadding,
        itemClickListener
    )
}
