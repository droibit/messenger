package com.droibit.looking2.core.data.source.local

import com.droibit.looking2.core.data.source.local.twitter.TwitterLocalStore
import com.droibit.looking2.core.data.source.local.twitter.TwitterLocalStoreImpl
import dagger.Binds
import dagger.Module

@Module(includes = [LocalStoreModule.BindingModule::class])
object LocalStoreModule {

    @Module
    internal interface BindingModule {

        @Binds
        fun bindTwitterLocalStore(store: TwitterLocalStoreImpl): TwitterLocalStore
    }
}