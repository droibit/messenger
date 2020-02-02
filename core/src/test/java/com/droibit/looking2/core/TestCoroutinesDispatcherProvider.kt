package com.droibit.looking2.core

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import kotlinx.coroutines.test.TestCoroutineDispatcher

object TestCoroutinesDispatcherProvider {

    operator fun invoke(): CoroutinesDispatcherProvider {
        return CoroutinesDispatcherProvider(
            main = TestCoroutineDispatcher(),
            computation = TestCoroutineDispatcher(),
            io = TestCoroutineDispatcher()
        )
    }
}