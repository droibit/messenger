package com.droibit.looking2.account.ui.twitter

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.wearable.view.AcceptDenyDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.droibit.looking2.account.R

class SignOutConfirmationDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    private val args: SignOutConfirmationDialogFragmentArgs by navArgs()

    private val viewModel: TwitterAccountListViewModel by navGraphViewModels(R.id.navigationTwitterAccountList)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val accountName = getString(
            R.string.account_twitter_account_name_with_at,
            args.account.name
        )
        return AcceptDenyDialog(requireContext()).also {
            it.setMessage(
                getString(
                    R.string.account_sign_out_message_sign_out_twitter_account,
                    accountName
                )
            )
            it.setNegativeButton(this)
            it.setPositiveButton(this)
        }
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            viewModel.signOutAccount(args.account)
        }
    }
}