package com.droibit.looking2.core.data.source.api

import android.content.Context
import android.support.wearable.authentication.OAuthClient
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object WearOAuthModule {
    @Provides
    fun provideOAuthClient(@Named("appContext") context: Context): OAuthClient {
        return OAuthClient.create(context)
    }

    @Named("wearCallbackUrl")
    @Provides
    fun provideWearCallbackUrl(@Named("appContext") context: Context): String {
        return OAuthClient.WEAR_REDIRECT_URL_PREFIX + context.packageName
    }
}