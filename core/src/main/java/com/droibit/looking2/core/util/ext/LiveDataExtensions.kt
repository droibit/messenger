package com.droibit.looking2.core.util.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.droibit.looking2.core.util.Event

inline fun <T> LiveData<Event<T>>.observeIfNotConsumed(
    owner: LifecycleOwner,
    crossinline onEventUnhandledContent: (T) -> Unit
) {
    this.observe(owner, Observer {
        it?.consume()?.let { value ->
            onEventUnhandledContent(value)
        }
    })
}

inline fun <T> LiveData<T>.observeNonNull(
    owner: LifecycleOwner,
    crossinline observer: (T) -> Unit
) {
    this.observe(owner, Observer {
        if (it != null) observer(it)
    })
}
