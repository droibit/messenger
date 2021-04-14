package com.droibit.looking2.core.ui.view

import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.view.InputDeviceCompat
import androidx.core.view.MotionEventCompat.AXIS_SCROLL
import androidx.core.view.ViewConfigurationCompat
import kotlin.math.roundToInt

/**
 * ref. https://developer.android.com/training/wearables/ui/rotary-input?hl=en
 */
class OnRotaryScrollListener : View.OnGenericMotionListener {
    override fun onGenericMotion(v: View, event: MotionEvent): Boolean {
        return if (event.action == MotionEvent.ACTION_SCROLL &&
            event.isFromSource(InputDeviceCompat.SOURCE_ROTARY_ENCODER)
        ) {
            // Don't forget the negation here
            val delta = -event.getAxisValue(AXIS_SCROLL) *
                ViewConfigurationCompat.getScaledVerticalScrollFactor(
                    ViewConfiguration.get(v.context), v.context
                )
            // Swap these axes to scroll horizontally instead
            v.scrollBy(0, delta.roundToInt())
            true
        } else {
            false
        }
    }
}
