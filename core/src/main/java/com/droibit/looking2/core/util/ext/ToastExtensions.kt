package com.droibit.looking2.core.util.ext

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.droibit.looking2.core.ui.ToastConvertible

fun Fragment.showShortToast(@StringRes resId: Int) {
    Toast.makeText(requireContext(), resId, Toast.LENGTH_SHORT).show()
}

fun Fragment.showShortToast(@StringRes resId: Int, vararg formatArgs: Any) {
    Toast.makeText(requireContext(), getString(resId, *formatArgs), Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(@StringRes resId: Int) {
    Toast.makeText(requireContext(), resId, Toast.LENGTH_LONG).show()
}

fun Fragment.showLongToast(@StringRes resId: Int, vararg formatArgs: Any) {
    Toast.makeText(requireContext(), getString(resId, *formatArgs), Toast.LENGTH_LONG).show()
}

fun Fragment.showToast(toastConvertible: ToastConvertible) {
    Toast.makeText(
        requireContext(),
        toastConvertible.message(requireContext()),
        if (toastConvertible.longDuration) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
    ).show()
}