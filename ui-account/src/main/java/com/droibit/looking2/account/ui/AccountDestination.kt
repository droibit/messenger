package com.droibit.looking2.account.ui

import android.os.Bundle
import androidx.navigation.NavDirections
import com.droibit.looking2.account.R
import com.droibit.looking2.account.ui.AccountTrampolineFragmentDirections.Companion.toTwitterAccountList

internal enum class AccountDestination(val id: String) {
    SIGN_IN("sign_in"),
    ACCOUNT_LIST("accounts");

    fun toDirections(extra: Bundle): NavDirections {
        return when (this) {
            SIGN_IN -> object : NavDirections {
                override val actionId: Int = R.id.toTwitterSignIn
                override val arguments: Bundle = extra
            }
            ACCOUNT_LIST -> toTwitterAccountList()
        }
    }

    companion object {
        const val KEY_ID = "id"
        const val KEY_MUST_SIGN_IN = "mustSignIn"

        operator fun invoke(id: String): AccountDestination {
            return values().firstOrNull { it.id == id } ?: error("Unknown id($id).")
        }
    }
}
