package com.droibit.looking2.core.data.source

import com.droibit.looking2.core.data.source.api.ApiModule
import dagger.Module

@Module(
    includes = [
        ApiModule::class
    ]
)
interface SourceModule 