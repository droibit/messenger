package com.droibit.looking2.core.data.source

import com.droibit.looking2.core.data.source.api.twitter.TwitterModule
import dagger.Module

@Module(includes = [TwitterModule::class])
object SourceModule {

}