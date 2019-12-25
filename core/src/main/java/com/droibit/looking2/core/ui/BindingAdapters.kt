package com.droibit.looking2.core.ui

import android.view.View
import androidx.annotation.Px
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["visibleUnless", "requestFocusOnVisible"], requireAll = false)
fun bindVisibleUnless(view: View, visible: Boolean, requestFocus: Boolean = false) {
    view.isVisible = visible

    if (view.isVisible && requestFocus) {
        view.requestFocus()
    }
}

@BindingAdapter("goneUnless")
fun bindGoneUnless(view: View, gone: Boolean) {
    view.isGone = gone
}

@BindingAdapter("android:marginTop")
fun bindPaddingTop(view: View, @Px marginPx: Int) {
    view.updatePadding(top = marginPx)
}

@BindingAdapter("android:paddingBottom")
fun bindPaddingBottom(view: View, @Px marginPx: Int) {
    view.updatePadding(bottom = marginPx)
}

@BindingAdapter("enabled")
fun bindEnabled(view: View, enabled: Boolean) {
    view.isEnabled = enabled
}