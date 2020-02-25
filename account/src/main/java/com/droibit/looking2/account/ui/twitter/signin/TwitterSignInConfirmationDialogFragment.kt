package com.droibit.looking2.account.ui.twitter.signin

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.os.Bundle
import android.os.Parcelable
import android.support.wearable.view.AcceptDenyDialog
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.droibit.looking2.account.R
import com.droibit.looking2.core.ui.dialog.DialogButton
import com.droibit.looking2.core.ui.dialog.DialogButtonResult
import com.droibit.looking2.core.util.ext.setResult
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TwitterSignInConfirmationDialogResult(
    override val button: Int
) : DialogButtonResult, Parcelable

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
        setResult(args.resultKey, TwitterSignInConfirmationDialogResult(which))
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        setResult(args.resultKey, TwitterSignInConfirmationDialogResult(BUTTON_NEGATIVE))
    }
}