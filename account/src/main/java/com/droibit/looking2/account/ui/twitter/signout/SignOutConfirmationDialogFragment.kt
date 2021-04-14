package com.droibit.looking2.account.ui.twitter.signout

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.os.Bundle
import android.support.wearable.view.AcceptDenyDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import com.droibit.looking2.account.R
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.ui.dialog.DialogButton
import com.droibit.looking2.core.ui.dialog.DialogButtonResult
import com.droibit.looking2.core.ui.dialog.DialogButtonResult.Companion.KEY_BUTTON

data class SignOutConfirmationDialogResult(
    @DialogButton override val button: Int,
    val account: TwitterAccount
) : DialogButtonResult {

    constructor(data: Bundle) : this(
        button = data.getInt(KEY_BUTTON),
        account = data.getSerializable(KEY_ACCOUNT) as TwitterAccount
    )

    override fun toBundle() = bundleOf(
        KEY_BUTTON to button,
        KEY_ACCOUNT to account
    )

    companion object {
        private const val KEY_ACCOUNT = "account"
    }
}

class SignOutConfirmationDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    private val args: SignOutConfirmationDialogFragmentArgs by navArgs()

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

    override fun onClick(dialog: DialogInterface, @DialogButton which: Int) {
        setDialogResult(which)
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        setDialogResult(which = BUTTON_NEGATIVE)
    }

    private fun setDialogResult(@DialogButton which: Int) {
        val result = SignOutConfirmationDialogResult(which, args.account)
        setFragmentResult(args.requestKey, result.toBundle())
    }
}
