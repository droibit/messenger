package com.droibit.looking2.core.data.source.remote

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.mock
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> mockSuccessfulCall(body: T): Call<T> {
    return mock {
        on { this.enqueue(any()) } doAnswer {
            @Suppress("UNCHECKED_CAST")
            val callback = it.arguments.first() as Callback<T>
            callback.onResponse(
                this.mock,
                Response.success(body)
            )
        }
    }
}

fun <T> mockErrorCall(statusCode: Int, errorBody: ResponseBody): Call<T> {
    return mock {
        on { this.enqueue(any()) } doAnswer {
            @Suppress("UNCHECKED_CAST")
            val callback = it.arguments.first() as Callback<T>
            callback.onResponse(
                this.mock,
                Response.error(statusCode, errorBody)
            )
        }
    }
}

fun <T> mockErrorCall(error: Throwable): Call<T> {
    return mock {
        on { this.enqueue(any()) } doAnswer {
            @Suppress("UNCHECKED_CAST")
            val callback = it.arguments.first() as Callback<T>
            callback.onFailure(this.mock, error)
        }
    }
}