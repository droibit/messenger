package com.droibit.looking2.core.data.repository

import com.droibit.looking2.core.data.source.SourceModule
import dagger.Module

@Module(includes = [SourceModule::class])
object RepositoryModule {
}