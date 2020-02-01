package com.droibit.looking2.core.util

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Interceptor
import java.util.Optional
import com.facebook.stetho.Stetho as FacebookStetho

object Stetho {

    fun initialize(context: Context) {
        FacebookStetho.initializeWithDefaults(context)
    }

    fun interceptor(): Optional<Interceptor> {
        return Optional.of(StethoInterceptor())
    }
}