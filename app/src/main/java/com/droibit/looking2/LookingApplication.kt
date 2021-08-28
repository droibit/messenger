package com.droibit.looking2

import android.app.Application
import android.content.Context
import androidx.annotation.UiThread
import androidx.work.Configuration as WorkConfiguration
import androidx.work.WorkManager
import com.droibit.looking2.core.config.AppVersion
import com.droibit.looking2.core.data.TwitterBootstrap
import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.DaggerCoreComponent
import com.droibit.looking2.core.util.Stetho
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import timber.log.Timber

class LookingApplication : Application() {

    private val coreComponent: CoreComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerCoreComponent.factory()
            .create(
                application = this,
                debuggable = BuildConfig.DEBUG,
                appVersion = AppVersion(
                    name = BuildConfig.VERSION_NAME,
                    code = BuildConfig.VERSION_CODE
                )
            )
    }

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationComponent.factory()
            .create(coreComponent)
            .inject(this)
    }

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
        Timber.d("Bootstrapped")

        if (BuildConfig.DEBUG) {
            Firebase.crashlytics.setCrashlyticsCollectionEnabled(false)
        }
    }

    companion object {
        @UiThread
        fun coreComponent(context: Context) =
            (context.applicationContext as LookingApplication).coreComponent
    }
}

@UiThread
fun Context.coreComponent() = LookingApplication.coreComponent(this)
