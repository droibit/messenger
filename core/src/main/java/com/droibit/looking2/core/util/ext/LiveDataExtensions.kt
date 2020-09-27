package com.droibit.looking2.core.util.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.droibit.looking2.core.util.Event

inline fun <T> LiveData<Event<T>>.observeEvent(
    owner: LifecycleOwner,
    crossinline onEventUnhandledContent: (T) -> Unit
) {
    this.observe(
        owner,
        Observer {
            it?.consume()?.let { value ->
                onEventUnhandledContent(value)
            }
        }
    )
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> LiveData<T>.requireValue(): T {
    return requireNotNull(value)
}
