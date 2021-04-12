package com.droibit.looking2.core.util

import android.content.Context
import java.util.Optional
import okhttp3.Interceptor

object Stetho {

    fun initialize(context: Context) = Unit

    fun interceptor(): Optional<Interceptor> {
        return Optional.empty()
    }
}
