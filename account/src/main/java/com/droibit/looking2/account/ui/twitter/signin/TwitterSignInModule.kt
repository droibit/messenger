package com.droibit.looking2.account.ui.twitter.signin

import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.di.key.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

// TODO: Should delete.
@Deprecated("Migrate to dagger hilt.")
@Module(includes = [TwitterSignInModule.BindingModule::class])
object TwitterSignInModule {

    @Module
    interface BindingModule {

        @Binds
        @IntoMap
        @ViewModelKey(TwitterSignInViewModel::class)
        fun bindTwitterSignInViewModel(viewModel: TwitterSignInViewModel): ViewModel
    }
}
