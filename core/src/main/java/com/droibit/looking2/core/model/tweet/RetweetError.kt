package com.droibit.looking2.core.model.tweet

// TODO: Consider adding auth error & limited api error.
sealed class RetweetError(message: String? = null) : Exception(message) {
    class Network : RetweetError()
    class UnExpected : RetweetError()
}