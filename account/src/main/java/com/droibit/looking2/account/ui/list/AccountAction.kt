package com.droibit.looking2.account.ui.list

import androidx.annotation.IdRes
import com.droibit.looking2.account.R

enum class AccountAction(@IdRes val id: Int) {
    SWITCH_ACCOUNT(id = R.id.account_action_switch),
    SIGN_OUT(id = R.id.account_action_sign_out);

    companion object {

        operator fun invoke(@IdRes itemId: Int) = values().first { it.id == itemId }
    }
}