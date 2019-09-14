package com.droibit.looking2.core.util

import android.content.Context
import com.facebook.stetho.Stetho as FacebookStetho

object Stetho {

    fun initialize(context: Context) {
        FacebookStetho.initializeWithDefaults(context)
    }
}