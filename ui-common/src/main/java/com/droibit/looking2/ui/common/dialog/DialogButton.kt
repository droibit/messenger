package com.droibit.looking2.ui.common.dialog

import android.content.DialogInterface
import androidx.annotation.IntDef

@IntDef(
    DialogInterface.BUTTON_NEGATIVE,
    DialogInterface.BUTTON_NEUTRAL,
    DialogInterface.BUTTON_POSITIVE
)
@Retention(AnnotationRetention.RUNTIME)
annotation class DialogButton
