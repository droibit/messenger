package com.droibit.looking2.ui.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.droibit.looking2.core.di.key.ViewModelKey
import com.droibit.looking2.core.util.lifecycle.DaggerViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [LaunchModule.BindingModule::class])
internal object LaunchModule {

    @Module
    interface BindingModule {

        @Binds
        @IntoMap
        @ViewModelKey(LaunchViewModel::class)
        fun bindLaunchViwModel(viewModel: LaunchViewModel): ViewModel

        @Binds
        fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
    }
}
