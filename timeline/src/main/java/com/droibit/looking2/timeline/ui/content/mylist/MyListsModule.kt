package com.droibit.looking2.timeline.ui.content.mylist

import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.di.key.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [MyListsModule.BindingModule::class])
object MyListsModule {

    @Provides
    @JvmStatic
    fun provideMyListAdapter(fragment: MyListsFragment): MyListAdapter {
        return MyListAdapter(
            fragment.requireContext(),
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