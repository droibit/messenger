package com.droibit.looking2.core.data.source

import com.droibit.looking2.core.data.source.api.ApiModule
import com.droibit.looking2.core.data.source.firebase.FirebaseModule
import com.droibit.looking2.core.data.source.local.LocalStoreModule
import dagger.Module

@Module(
    includes = [
        ApiModule::class,
        LocalStoreModule::class,
        FirebaseModule::class
    ]
)
interface SourceModule