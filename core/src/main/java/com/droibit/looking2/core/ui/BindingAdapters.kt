package com.droibit.looking2.core.ui

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
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