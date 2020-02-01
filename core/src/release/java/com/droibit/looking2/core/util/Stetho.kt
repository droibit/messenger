package com.droibit.looking2.core.util

import android.content.Context
import okhttp3.Interceptor
import java.util.Optional

object Stetho {

    fun initialize(context: Context) = Unit

    fun interceptor(): Optional<Interceptor> {
        return Optional.empty()
    }
}