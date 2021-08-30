package com.droibit.looking2.core.data.source.remote.firebase

import android.annotation.SuppressLint
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {

    @SuppressLint("MissingPermission")
    @Provides
    @Singleton
    fun provide(): FirebaseAnalytics {
        return Firebase.analytics
    }
}
