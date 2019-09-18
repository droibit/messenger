package com.droibit.looking2.account.ui.signin.twitter

import androidx.annotation.StringRes

sealed class TwitterAuthenticationResult {
    sealed class FailureType {
        object Network : FailureType()
        class PlayServices(val errorStatusCode: Int) : FailureType()
        class UnExpected(@StringRes val messageResId: Int) : FailureType()
    }

    class InProgress(val authenticatingOnPhone: Boolean) : TwitterAuthenticationResult()
    object Success : TwitterAuthenticationResult()
    class Failure(val type: FailureType) : TwitterAuthenticationResult()

    val isInProgress: Boolean get() = this is InProgress
}