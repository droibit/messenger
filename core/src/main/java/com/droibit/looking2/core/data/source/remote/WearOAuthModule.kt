package com.droibit.looking2.core.data.source.remote

import android.content.Context
import androidx.wear.phone.interactions.authentication.RemoteAuthClient
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object WearOAuthModule {

    @Provides
    fun provideWearRemoteAuthClient(@Named("appContext") context: Context): RemoteAuthClient {
        return RemoteAuthClient.create(context)
    }
}
