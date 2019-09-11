package com.droibit.looking2.core.model.account

sealed class AuthenticationResult {
    object WillAuthenticateOnPhone : AuthenticationResult()
    object Success : AuthenticationResult()
    class Failure(val error: AuthenticationError) : AuthenticationResult() {
        override fun toString(): String = error.toString()
    }
}