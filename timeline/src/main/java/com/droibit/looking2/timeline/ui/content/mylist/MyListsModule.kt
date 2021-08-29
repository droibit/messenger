package com.droibit.looking2.timeline.ui.content.mylist

import android.view.LayoutInflater
import androidx.lifecycle.LifecycleOwner
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import javax.inject.Named
import javax.inject.Provider

@InstallIn(FragmentComponent::class)
@Module
object MyListsModule {

    @Named("fragment")
    @Provides
    fun provideLifecycleOwner(fragment: MyListsFragment): LifecycleOwner {
        return fragment.viewLifecycleOwner
    }

    @Provides
    fun provideMyListAdapter(
        fragment: MyListsFragment,
        contentPadding: ShapeAwareContentPadding,
        @Named("fragment") lifecycleOwner: Provider<LifecycleOwner>
    ): MyListAdapter {
        return MyListAdapter(
            LayoutInflater.from(fragment.requireContext()),
            contentPadding,
            lifecycleOwner,
            itemClickListener = fragment::onUserListClick
        )
    }
}
