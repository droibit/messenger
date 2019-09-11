package com.droibit.looking2.core.data.repository

import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.data.repository.account.AccountRepositoryImpl
import com.droibit.looking2.core.data.source.SourceModule
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(includes = [
    SourceModule::class,
    RepositoryModule.BindingModule::class
])
object RepositoryModule {

    @Module
    internal interface BindingModule {

        @Singleton
        @Binds
        fun bindAccountRepository(repository: AccountRepositoryImpl): AccountRepository
    }

    interface Provider {

        fun provideAccountRepository(): AccountRepository
    }
}