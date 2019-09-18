package com.droibit.looking2.core.util.ext

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.droibit.looking2.core.R

fun Fragment.showShortToast(@StringRes resId: Int) {
    Toast.makeText(requireContext(), resId, Toast.LENGTH_SHORT).show()
}

fun Fragment.showLongToast(@StringRes resId: Int) {
    Toast.makeText(requireContext(), resId, Toast.LENGTH_LONG).show()
}

fun Fragment.showNetworkErrorToast() {
    showShortToast(R.string.error_message_network_disconnected)
}