package com.droibit.looking2.core.ui.view

import android.support.wearable.input.RotaryEncoder
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_SCROLL
import android.view.View
import kotlin.math.roundToInt

/**
 * ref. https://developer.android.com/training/wearables/ui/rotary-input?hl=en
 */
class OnRotaryScrollListener : View.OnGenericMotionListener {
    override fun onGenericMotion(v: View, event: MotionEvent): Boolean {
        return if (event.action == ACTION_SCROLL && RotaryEncoder.isFromRotaryEncoder(event)) {
            // Don't forget the negation here
            val delta =
                -RotaryEncoder.getRotaryAxisValue(event) * RotaryEncoder.getScaledScrollFactor(
                    v.context
                )

            // Swap these axes if you want to do horizontal scrolling instead
            v.scrollBy(0, delta.roundToInt())
            true
        } else {
            false
        }
    }
}