package com.droibit.looking2.account.ui.signin.twitter

import androidx.annotation.StringRes
import com.droibit.looking2.core.util.Event

sealed class TwitterAuthenticationResult {
    sealed class FailureType {
        object Network : FailureType()
        class PlayServices(val errorStatusCode: Int) : FailureType()
        class UnExpected(@StringRes val messageResId: Int) : FailureType()
    }

    object InProgress : TwitterAuthenticationResult()
    class Success(val value: Event<Unit>) : TwitterAuthenticationResult()
    data class Failure(val failureType: Event<FailureType>) : TwitterAuthenticationResult()
}