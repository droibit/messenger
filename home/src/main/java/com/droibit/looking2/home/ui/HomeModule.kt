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
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.multibindings.IntoMap

@InstallIn(FragmentComponent::class)
@Module(includes = [HomeModule.BindingModule::class])
object HomeModule {

    @Provides
    fun provideActionListAdapter(fragment: HomeFragment): ActionItemListAdapter {
        return ActionItemListAdapter(
            fragment.requireContext(),
            menuRes = R.menu.navigation,
            itemClickListener = fragment::onActionItemClick
        )
    }

    @Deprecated("Migrate to dagger hilt.")
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
