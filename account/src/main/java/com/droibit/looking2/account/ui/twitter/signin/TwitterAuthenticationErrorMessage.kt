package com.droibit.looking2.account.ui.twitter.signin

import androidx.annotation.StringRes
import com.droibit.looking2.account.R
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.ui.StringResourceToast
import com.droibit.looking2.core.ui.ToastConvertible

sealed class TwitterAuthenticationErrorMessage : Throwable() {
    data class Toast(
        private val value: ToastConvertible
    ) : TwitterAuthenticationErrorMessage(), ToastConvertible by value

    data class FailureConfirmation(@StringRes val messageResId: Int) :
        TwitterAuthenticationErrorMessage()

    companion object {
        operator fun invoke(source: AuthenticationError): TwitterAuthenticationErrorMessage {
            return when (source) {
                is AuthenticationError.Network -> Toast(StringResourceToast.Network)
                is AuthenticationError.Phone -> {
                    // TODO: Detailed error messages.
                    FailureConfirmation(R.string.account_sign_in_error_message_unexpected)
                }
                is AuthenticationError.UnExpected -> {
                    FailureConfirmation(R.string.account_sign_in_error_message_unexpected)
                }
            }
        }
    }
}
