package com.github.droibit.messenger.sample

import android.app.Application
import timber.log.Timber

class MessengerApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Timber.plant(Timber.DebugTree())
    }
  }
}
