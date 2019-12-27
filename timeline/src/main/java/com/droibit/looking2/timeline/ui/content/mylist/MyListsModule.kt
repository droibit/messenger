package com.droibit.looking2.timeline.ui.content.mylist

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.di.key.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Named
import javax.inject.Provider

@Module(includes = [MyListsModule.BindingModule::class])
object MyListsModule {

    @Named("fragment")
    @Provides
    fun provideLifecycleOwner(fragment: MyListsFragment): LifecycleOwner {
        return fragment
    }

    @Provides
    fun provideMyListAdapter(
        fragment: MyListsFragment,
        @Named("fragment") lifecycleOwner: Provider<LifecycleOwner>
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