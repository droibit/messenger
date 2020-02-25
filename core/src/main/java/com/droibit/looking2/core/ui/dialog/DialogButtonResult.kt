package com.droibit.looking2.core.ui.dialog

import android.content.DialogInterface

interface DialogButtonResult {
    @DialogButton
    val button: Int
}

val DialogButtonResult.isPositive: Boolean
    get() = button == DialogInterface.BUTTON_POSITIVE

val DialogButtonResult.isNegative: Boolean
    get() = button == DialogInterface.BUTTON_NEGATIVE