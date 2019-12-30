package com.droibit.looking2.account.ui.signin.twitter

import androidx.annotation.StringRes
import com.droibit.looking2.account.R
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.util.checker.PlayServicesChecker

sealed class TwitterAuthenticationError : Throwable() {
    object Network : TwitterAuthenticationError()
    class PlayServices(val statusCode: Int) : TwitterAuthenticationError()
    class UnExpected(@StringRes val messageResId: Int) : TwitterAuthenticationError()

    companion object {
        operator fun invoke(
            source: AuthenticationError,
            playServicesChecker: PlayServicesChecker
        ): TwitterAuthenticationError {
            return when (source) {
                is AuthenticationError.Network -> Network
                is AuthenticationError.PlayServices -> {
                    val status = playServicesChecker.checkStatusCode(source.statusCode)
                        as PlayServicesChecker.Status.Error
                    if (status.isUserResolvableError) {
                        PlayServices(statusCode = source.statusCode)
                    } else {
                        UnExpected(R.string.account_sign_in_error_message_unexpected)
                    }
                }
                is AuthenticationError.UnExpected -> {
                    UnExpected(R.string.account_sign_in_error_message_unexpected)
                }
            }
        }
    }
}