package com.droibit.looking2.core.util.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.droibit.looking2.core.util.Event

inline fun <T> LiveData<Event<T>>.observeIfNotHandled(
    owner: LifecycleOwner,
    crossinline onEventUnhandledContent: (T) -> Unit
) {
    this.observe(owner, Observer {
        it?.peek()?.let { value ->
            onEventUnhandledContent(value)
        }
    })
}