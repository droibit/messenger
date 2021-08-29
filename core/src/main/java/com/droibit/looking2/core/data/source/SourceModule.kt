package com.droibit.looking2.core.data.source

import com.droibit.looking2.core.data.source.local.LocalStoreModule
import com.droibit.looking2.core.data.source.remote.RemoteSourceModule
import com.droibit.looking2.core.data.source.remote.WearOAuthModule
import com.droibit.looking2.core.data.source.remote.firebase.FirebaseModule
import com.droibit.looking2.core.data.source.remote.twitter.api.TwitterApiModule
import dagger.Module
import dagger.hilt.migration.DisableInstallInCheck

// TODO: Should delete.
@DisableInstallInCheck
@Deprecated("Migrate to dagger hilt.")
@Module(
    includes = [
        RemoteSourceModule::class,
        LocalStoreModule::class,
        FirebaseModule::class,
        TwitterApiModule::class,
        WearOAuthModule::class
    ]
)
interface SourceModule
