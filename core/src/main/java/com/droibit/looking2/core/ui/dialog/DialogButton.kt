package com.droibit.looking2.core.ui.dialog

import android.content.DialogInterface
import androidx.annotation.IntDef

@IntDef(
    DialogInterface.BUTTON_NEGATIVE,
    DialogInterface.BUTTON_NEUTRAL,
    DialogInterface.BUTTON_POSITIVE
)
@Retention(AnnotationRetention.RUNTIME)
annotation class DialogButton