package com.droibit.looking2.account.ui.signin

import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.BUTTON_NEGATIVE
import android.os.Bundle
import android.support.wearable.view.AcceptDenyDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.droibit.looking2.core.ui.dialog.DialogId
import com.droibit.looking2.core.ui.dialog.DialogViewModel

class SignInConfirmationDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    private val args: SignInConfirmationDialogFragmentArgs by navArgs()

    private val viewModel: DialogViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AcceptDenyDialog(context).also {
            it.setMessage(args.message)
            it.setNegativeButton(this)
            it.setPositiveButton(this)
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onClick(dialog, which = BUTTON_NEGATIVE)
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        val destId = requireNotNull(findNavController().currentDestination).id
        viewModel.doAction(DialogId(destId), which)
    }
}