package com.droibit.looking2.core.model.account

sealed class AuthenticationError(message: String? = null) : Exception(message) {
    class Network : AuthenticationError()
    data class PlayServices(val statusCode: Int) : AuthenticationError()
    class UnExpected : AuthenticationError()
}