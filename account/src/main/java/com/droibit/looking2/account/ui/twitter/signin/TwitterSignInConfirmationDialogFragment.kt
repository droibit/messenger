package com.droibit.looking2.account.ui.twitter.signin

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.wearable.view.AcceptDenyDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.navGraphViewModels
import com.droibit.looking2.account.R

class TwitterSignInConfirmationDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    private val signInViewModel: TwitterSignInViewModel by navGraphViewModels(R.id.navigationTwitterSignIn)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AcceptDenyDialog(requireContext()).also {
            it.setMessage(getString(R.string.account_sign_in_message_phone_preparation))
            it.setNegativeButton(this)
            it.setPositiveButton(this)
        }
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            signInViewModel.authenticate()
        }
    }
}