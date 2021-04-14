package com.droibit.looking2.core.util.ext

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.droibit.looking2.core.ui.ToastConvertible

fun Fragment.showToast(toastConvertible: ToastConvertible) {
    Toast.makeText(
        requireContext(),
        toastConvertible.message(requireContext()),
        if (toastConvertible.longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}
