package com.droibit.looking2.timeline.ui.content.mylist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.di.key.ViewModelKey
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [MyListsModule.BindingModule::class])
object MyListsModule {

    @Provides
    fun provideLifecycleOwner(fragment: MyListsFragment): LifecycleOwner {
        return fragment.viewLifecycleOwner
    }

    @Provides
    fun provideMyListAdapter(
        fragment: MyListsFragment,
        lifecycleOwner: Lazy<LifecycleOwner>
    ): MyListAdapter {
        return MyListAdapter(
            fragment.requireContext(),
            lifecycleOwner,
            itemClickListener = fragment::onUserListClick
        )
    }

    @Module
    interface BindingModule {

        @Binds
        @IntoMap
        @ViewModelKey(MyListsViewModel::class)
        fun bindMyListsViewModel(ViewModel: MyListsViewModel): ViewModel
    }
}