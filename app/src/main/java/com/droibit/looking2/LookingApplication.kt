package com.droibit.looking2

import android.app.Application
import androidx.work.Configuration as WorkConfiguration
import androidx.work.WorkManager
import com.droibit.looking2.core.data.TwitterBootstrap
import com.droibit.looking2.core.util.Stetho
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import timber.log.Timber

@HiltAndroidApp
class LookingApplication : Application() {

    @Inject
    fun bootstrap(
        timberTree: Timber.Tree,
        twitterBootstrap: TwitterBootstrap,
        workConfiguration: WorkConfiguration
    ) {
        Timber.plant(timberTree)
        twitterBootstrap.initialize()
        WorkManager.initialize(this, workConfiguration)
        Stetho.initialize(this)

        if (BuildConfig.DEBUG) {
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(false)
        }
        Timber.d("Bootstrapped")
    }
}
