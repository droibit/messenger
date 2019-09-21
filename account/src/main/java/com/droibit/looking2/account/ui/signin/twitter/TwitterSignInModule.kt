package com.droibit.looking2.account.ui.signin.twitter

import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.di.key.ViewModelKey
import com.droibit.looking2.core.ui.dialog.DialogViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [TwitterSignInModule.BindingModule::class])
object TwitterSignInModule {

    @Module
    interface BindingModule {

        @Binds
        @IntoMap
        @ViewModelKey(DialogViewModel::class)
        fun bindDialogViewModel(viewModel: DialogViewModel): ViewModel

        @Binds
        @IntoMap
        @ViewModelKey(TwitterSignInViewModel::class)
        fun bindTwitterSignInViewModel(viewModel: TwitterSignInViewModel): ViewModel
    }
}