package com.droibit.looking2.core.ui

import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["visibleUnless", "requestFocusOnVisible"], requireAll = false)
fun bindVisibleUnless(view: View, visible: Boolean, requestFocus: Boolean = false) {
    view.isVisible = visible

    if (view.isVisible && requestFocus) {
        view.requestFocus()
    }
}

@BindingAdapter(value = ["goneUnless", "requestFocusOnVisible"], requireAll = false)
fun bindGoneUnless(view: View, gone: Boolean, requestFocus: Boolean = false) {
    view.isGone = gone

    if (view.isVisible && requestFocus) {
        view.requestFocus()
    }
}

@BindingAdapter("android:marginTop")
fun bindPaddingTop(view: View, @Px spacePx: Int) {
    view.updatePadding(top = spacePx)
}

@BindingAdapter("android:paddingBottom")
fun bindPaddingBottom(view: View, @Px spacePx: Int) {
    view.updatePadding(bottom = spacePx)
}

@BindingAdapter("android:paddingStart")
fun bindPaddingStart(view: View, @Px spacePx: Int) {
    view.updatePadding(left = spacePx)
}

@BindingAdapter("android:paddingEnd")
fun bindPaddingEnd(view: View, @Px spacePx: Int) {
    view.updatePadding(right = spacePx)
}

@BindingAdapter("android:layout_marginTop")
fun bindMarginTop(view: View, @Px spacePx: Int) {
    view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = spacePx
    }
}

@BindingAdapter("android:layout_marginBottom")
fun bindMarginBottom(view: View, @Px spacePx: Int) {
    view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        bottomMargin = spacePx
    }
}

@BindingAdapter("enabled")
fun bindEnabled(view: View, enabled: Boolean) {
    view.isEnabled = enabled
}

@BindingAdapter("activated")
fun bindActivated(view: View, activated: Boolean) {
    view.isActivated = activated
}