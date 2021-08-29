package com.droibit.looking2.core.data.source.remote

import android.content.Context
import androidx.wear.phone.interactions.authentication.RemoteAuthClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object WearOAuthModule {

    @Provides
    fun provideWearRemoteAuthClient(@ApplicationContext context: Context): RemoteAuthClient {
        return RemoteAuthClient.create(context)
    }
}
