package com.droibit.looking2.account.ui.twitter

import androidx.annotation.StringRes
import com.droibit.looking2.account.R

data class LimitSignInErrorMessage(
    val maxNumOfAccounts: Int
) {
    @get:StringRes
    val resId: Int = R.string.account_sign_in_error_message_unable_to_add_account
}