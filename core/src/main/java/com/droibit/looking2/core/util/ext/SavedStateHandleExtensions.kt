package com.droibit.looking2.core.util.ext

import androidx.lifecycle.SavedStateHandle

fun <T> SavedStateHandle.consume(key: String): T? {
    return get<T>(key)?.also { remove<T>(key) }
}
