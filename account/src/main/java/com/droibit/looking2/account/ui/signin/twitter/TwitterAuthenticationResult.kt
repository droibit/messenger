package com.droibit.looking2.account.ui.signin.twitter

import androidx.annotation.StringRes

sealed class TwitterAuthenticationResult {
    sealed class ErrorType {
        object Network : ErrorType()
        class PlayServices(val errorStatusCode: Int) : ErrorType()
        class UnExpected(@StringRes val messageResId: Int) : ErrorType()
    }

    class InProgress(val authenticatingOnPhone: Boolean) : TwitterAuthenticationResult()
    object Success : TwitterAuthenticationResult()
    class Failure(val type: ErrorType) : TwitterAuthenticationResult()
}