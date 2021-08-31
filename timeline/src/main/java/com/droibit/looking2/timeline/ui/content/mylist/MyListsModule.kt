package com.droibit.looking2.timeline.ui.content.mylist

import androidx.fragment.app.Fragment
import com.droibit.looking2.ui.common.view.ShapeAwareContentPadding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module
object MyListsModule {

    @Provides
    fun provideUserListItemClickListener(fragment: Fragment) =
        fragment as MyListAdapter.OnItemClickListener

    @Provides
    fun provideMyListAdapter(
        contentPadding: ShapeAwareContentPadding,
        fragment: Fragment,
        itemCLickListener: MyListAdapter.OnItemClickListener
    ) = MyListAdapter(
        contentPadding,
        lifecycleOwner = { fragment.viewLifecycleOwner },
        itemClickListener = itemCLickListener
    )
}
