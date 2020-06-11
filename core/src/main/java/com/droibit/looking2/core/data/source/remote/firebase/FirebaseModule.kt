package com.droibit.looking2.core.data.source.remote.firebase

import android.annotation.SuppressLint
import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
object FirebaseModule {

    @SuppressLint("MissingPermission")
    @Provides
    @Singleton
    fun provide(@Named("appContext") context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }
}