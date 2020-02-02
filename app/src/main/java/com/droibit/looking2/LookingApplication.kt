package com.droibit.looking2

import android.app.Application
import android.content.Context
import androidx.work.WorkManager
import com.droibit.looking2.core.config.AppVersion
import com.droibit.looking2.core.data.TwitterBootstrap
import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.DaggerCoreComponent
import com.droibit.looking2.core.util.Stetho
import timber.log.Timber
import javax.inject.Inject
import androidx.work.Configuration as WorkConfiguration

class LookingApplication : Application() {

    private val coreComponent: CoreComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerCoreComponent.builder()
            .debuggable(BuildConfig.DEBUG)
            .appVersion(
                AppVersion(
                    name = BuildConfig.VERSION_NAME,
                    code = BuildConfig.VERSION_CODE
                )
            )
            .application(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationComponent.builder()
            .core(coreComponent)
            .build()
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
    }

    companion object {
        @JvmStatic
        fun coreComponent(context: Context) =
            (context.applicationContext as LookingApplication).coreComponent
    }
}

fun Context.coreComponent() = LookingApplication.coreComponent(this)