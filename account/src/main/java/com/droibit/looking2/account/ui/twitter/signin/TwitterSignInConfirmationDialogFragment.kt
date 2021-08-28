package com.droibit.looking2.account.ui.twitter.signin

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.droibit.looking2.account.R
import com.droibit.looking2.core.ui.dialog.DialogButton
import com.droibit.looking2.core.ui.dialog.DialogButtonResult
import com.droibit.looking2.core.ui.dialog.DialogButtonResult.Companion.KEY_BUTTON
import com.github.droibit.support.wearable.view.AcceptDenyDialog

data class TwitterSignInConfirmationDialogResult(
    override val button: Int
) : DialogButtonResult {
    constructor(data: Bundle) : this(data.getInt(KEY_BUTTON))

    override fun toBundle() = bundleOf(KEY_BUTTON to button)
}

class TwitterSignInConfirmationDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    private val args: TwitterSignInConfirmationDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AcceptDenyDialog(requireContext()).also {
            it.setMessage(getString(R.string.account_sign_in_message_phone_preparation))
            it.setNegativeButton(this)
            it.setPositiveButton(this)
        }
    }

    override fun onClick(dialog: DialogInterface, @DialogButton which: Int) {
        setDialogResult(which)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        setDialogResult(which = BUTTON_NEGATIVE)
    }

    private fun setDialogResult(@DialogButton which: Int) {
        val result = TwitterSignInConfirmationDialogResult(button = which)
        setFragmentResult(args.requestKey, result.toBundle())
    }
}
