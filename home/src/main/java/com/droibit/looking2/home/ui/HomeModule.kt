package com.droibit.looking2.home.ui

import com.droibit.looking2.core.ui.widget.ActionItemListAdapter
import com.droibit.looking2.home.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module
object HomeModule {

    @Provides
    fun provideActionListAdapter(fragment: HomeFragment): ActionItemListAdapter {
        return ActionItemListAdapter(
            fragment.requireContext(),
            menuRes = R.menu.navigation,
            itemClickListener = fragment::onActionItemClick
        )
    }
}
