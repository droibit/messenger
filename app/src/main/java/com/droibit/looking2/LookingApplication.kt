package com.droibit.looking2

import android.app.Activity
import android.app.Application
import android.content.Context
import com.droibit.looking2.core.di.CoreComponent
import com.droibit.looking2.core.di.DaggerCoreComponent
import timber.log.Timber
import javax.inject.Inject

class LookingApplication: Application() {

    private val coreComponent: CoreComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerCoreComponent.builder()
            // .coreModule()
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationComponent.builder()
            .application(this)
            .debuggable(BuildConfig.DEBUG)
            .core(coreComponent)
            .build()
            .inject(this)
    }

    @Inject
    fun bootstrap(timberTree: Timber.Tree) {
        Timber.plant(timberTree)

        Timber.d("Bootstrapped")
    }

    companion object {
        @JvmStatic
        fun coreComponent(context: Context) =
            (context.applicationContext as LookingApplication).coreComponent
    }
}

fun Activity.coreComponent() = LookingApplication.coreComponent(this)