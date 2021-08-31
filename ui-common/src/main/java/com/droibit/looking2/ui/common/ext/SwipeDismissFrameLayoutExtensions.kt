package com.droibit.looking2.ui.common.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.wear.widget.SwipeDismissFrameLayout
import timber.log.Timber

fun SwipeDismissFrameLayout.addCallback(
    owner: LifecycleOwner,
    callback: SwipeDismissFrameLayout.Callback
) {
    val lifecycle = owner.lifecycle
    if (lifecycle.currentState == Lifecycle.State.DESTROYED) {
        return
    }
    val lifecycleCallback = LifecycleSwipeDismissFrameCallback(this, callback)
    addCallback(lifecycleCallback)
    lifecycle.addObserver(lifecycleCallback)
}

private class LifecycleSwipeDismissFrameCallback(
    private val layout: SwipeDismissFrameLayout,
    private val delegate: SwipeDismissFrameLayout.Callback
) : SwipeDismissFrameLayout.Callback(), LifecycleEventObserver {
    override fun onDismissed(layout: SwipeDismissFrameLayout) {
        delegate.onDismissed(layout)
    }

    override fun onSwipeCanceled(layout: SwipeDismissFrameLayout) {
        delegate.onSwipeCanceled(layout)
    }

    override fun onSwipeStarted(layout: SwipeDismissFrameLayout?) {
        delegate.onSwipeStarted(layout)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            layout.removeCallback(this)
            Timber.d("Remove SwipeDismissFrameLayout callback.")
        }
    }
}
