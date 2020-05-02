package com.droibit.looking2.core.ui.dialog

import android.content.DialogInterface
import android.os.Bundle

interface DialogButtonResult {
    @DialogButton
    val button: Int

    fun toBundle(): Bundle

    companion object {
        const val KEY_BUTTON = "DialogButtonResult#button"
    }
}

val DialogButtonResult.isPositive: Boolean
    get() = button == DialogInterface.BUTTON_POSITIVE

val DialogButtonResult.isNegative: Boolean
    get() = button == DialogInterface.BUTTON_NEGATIVE