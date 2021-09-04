package com.droibit.looking2.account.ui

import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.droibit.looking2.account.R
import com.droibit.looking2.account.ui.AccountDestination.ACCOUNT_LIST
import com.droibit.looking2.account.ui.AccountDestination.Companion.KEY_MUST_SIGN_IN
import com.droibit.looking2.account.ui.AccountDestination.SIGN_IN
import com.droibit.looking2.account.ui.AccountTrampolineFragmentDirections.Companion.toTwitterAccountList
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountDestinationTest {

    @Test
    fun toDirections_toSignInDirections() {
        val extra = bundleOf(KEY_MUST_SIGN_IN to true)
        val directions1 = SIGN_IN.toDirections(extra)
        assertThat(directions1.actionId).isEqualTo(R.id.toTwitterSignIn)
        assertThat(directions1.arguments.size()).isEqualTo(extra.size())
        assertThat(directions1.arguments.getBoolean(KEY_MUST_SIGN_IN)).isEqualTo(true)
    }

    @Test
    fun toDirections_toTwitterAccountList() {
        val directions = ACCOUNT_LIST.toDirections(extra = bundleOf())
        assertThat(directions).isEqualTo(toTwitterAccountList())
    }
}
