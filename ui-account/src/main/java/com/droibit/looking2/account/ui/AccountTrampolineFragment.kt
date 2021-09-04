package com.droibit.looking2.account.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.droibit.looking2.ui.common.ext.navigateSafely

class AccountTrampolineFragment : Fragment() {

    private val navArgs: AccountTrampolineFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val destination = AccountDestination(navArgs.id)
        findNavController().navigateSafely(
            destination.toDirections(extra = navArgs.toBundle())
        )
    }
}
