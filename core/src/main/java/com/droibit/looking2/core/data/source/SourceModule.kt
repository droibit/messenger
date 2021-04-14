package com.droibit.looking2.core.data.source

import com.droibit.looking2.core.data.source.local.LocalStoreModule
import com.droibit.looking2.core.data.source.remote.RemoteSourceModule
import com.droibit.looking2.core.data.source.remote.firebase.FirebaseModule
import dagger.Module

@Module(
    includes = [
        RemoteSourceModule::class,
        LocalStoreModule::class,
        FirebaseModule::class
    ]
)
interface SourceModule
