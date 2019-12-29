package com.droibit.looking2.core.util.ext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.droibit.looking2.core.util.Event

fun <T> LiveData<Result<T>>.toSuccessLiveData(): LiveData<T> {
    return MediatorLiveData<T>().apply {
        addSource(this@toSuccessLiveData) { result ->
            result.getOrNull()?.let {
                value = it
            }
        }
    }
}

fun <T> LiveData<Result<T>>.toSuccessEventLiveData(): LiveData<Event<T>> {
    return MediatorLiveData<Event<T>>().apply {
        addSource(this@toSuccessEventLiveData) { result ->
            result.getOrNull()?.let {
                value = Event(it)
            }
        }
    }
}

fun <T, E : Throwable> LiveData<Result<T>>.toErrorLiveData(): LiveData<E> {
    return MediatorLiveData<E>().apply {
        addSource(this@toErrorLiveData) { result ->
            result.exceptionOrNull()?.let {
                @Suppress("UNCHECKED_CAST")
                value = it as E
            }
        }
    }
}

fun <T, E : Throwable> LiveData<Result<T>>.toErrorEventLiveData(): LiveData<Event<E>> {
    return MediatorLiveData<Event<E>>()
        .apply {
            addSource(this@toErrorEventLiveData) { result ->
                result.exceptionOrNull()?.let {
                    @Suppress("UNCHECKED_CAST")
                    value = Event(it as E)
                }
            }
        }
}