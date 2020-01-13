package com.droibit.looking2.account.ui.twitter

import android.content.Context
import com.droibit.looking2.account.R
import com.droibit.looking2.core.ui.ToastConvertible

data class LimitSignInErrorMessage(
    private val maxNumOfAccounts: Int
) : ToastConvertible {
    override val longDuration: Boolean = true

    override fun message(context: Context): String {
        return context.getString(
            R.string.account_sign_in_error_message_unable_to_add_account,
            maxNumOfAccounts
        )
    }
}