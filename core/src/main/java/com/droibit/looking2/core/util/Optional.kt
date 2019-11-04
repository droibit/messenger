package com.droibit.looking2.core.util

data class Optional<T : Any>(private val value: T?) {

    val isPresent: Boolean get() = value != null

    fun get(): T? = value

    fun getValue(): T {
        checkNotNull(this.value)
        return value
    }
}