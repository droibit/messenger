package com.droibit.looking2.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.droibit.looking2.core.di.key.ViewModelKey
import com.droibit.looking2.core.ui.widget.ActionItemListAdapter
import com.droibit.looking2.core.util.lifecycle.DaggerViewModelFactory
import com.droibit.looking2.home.R
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [HomeModule.BindingModule::class])
object HomeModule {

    @Provides
    fun provideActionListAdapter(activity: HomeActivity): ActionItemListAdapter {
        return ActionItemListAdapter(
            activity,
            menuRes = R.menu.navigation,
            itemClickListener = activity::onActionItemClick
        )
    }

    @Module
    interface BindingModule {

        @Binds
        @IntoMap
        @ViewModelKey(HomeViewModel::class)
        fun bindTwitterSignInViewModel(viewModel: HomeViewModel): ViewModel

        @Binds
        fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
    }
}