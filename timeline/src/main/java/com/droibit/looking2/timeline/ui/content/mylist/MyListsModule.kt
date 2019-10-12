package com.droibit.looking2.timeline.ui.content.mylist

import dagger.Module
import dagger.Provides

@Module
object MyListsModule {

    @Provides
    @JvmStatic
    fun provideMyListAdapter(fragment: MyListsFragment): MyListAdapter {
        return MyListAdapter(
            fragment.requireContext(),
            itemClickListener = fragment::onUserListClick
        )
    }
}