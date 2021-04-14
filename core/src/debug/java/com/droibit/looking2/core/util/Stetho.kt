package com.droibit.looking2.core.util

import android.content.Context
import com.facebook.stetho.Stetho as FacebookStetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import java.util.Optional
import okhttp3.Interceptor

object Stetho {

    fun initialize(context: Context) {
        FacebookStetho.initializeWithDefaults(context)
    }

    fun interceptor(): Optional<Interceptor> {
        return Optional.of(StethoInterceptor())
    }
}
