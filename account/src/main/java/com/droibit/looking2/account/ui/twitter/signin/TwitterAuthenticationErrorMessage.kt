package com.droibit.looking2.account.ui.twitter.signin

import androidx.annotation.StringRes
import com.droibit.looking2.account.R
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.ui.StringResourceToast
import com.droibit.looking2.core.ui.ToastConvertible
import com.droibit.looking2.core.util.checker.PlayServicesChecker
import com.droibit.looking2.core.util.checker.PlayServicesChecker.Status.Error as PlayServicesError

sealed class TwitterAuthenticationErrorMessage : Throwable() {
    data class Toast(
        private val value: ToastConvertible
    ) : TwitterAuthenticationErrorMessage(), ToastConvertible by value

    data class PlayServicesDialog(val statusCode: Int) : TwitterAuthenticationErrorMessage()
    data class FailureConfirmation(@StringRes val messageResId: Int) :
        TwitterAuthenticationErrorMessage()

    companion object {
        operator fun invoke(
            source: AuthenticationError,
            playServicesChecker: PlayServicesChecker
        ): TwitterAuthenticationErrorMessage {
            return when (source) {
                is AuthenticationError.Network -> Toast(StringResourceToast.Network)
                is AuthenticationError.PlayServices -> {
                    val status =
                        playServicesChecker.checkStatusCode(source.statusCode) as PlayServicesError
                    if (status.isUserResolvableError) {
                        PlayServicesDialog(statusCode = source.statusCode)
                    } else {
                        FailureConfirmation(R.string.account_sign_in_error_message_unexpected)
                    }
                }
                is AuthenticationError.UnExpected -> {
                    FailureConfirmation(R.string.account_sign_in_error_message_unexpected)
                }
            }
        }
    }
}
