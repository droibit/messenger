package com.droibit.looking2.core.ui.view

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.Size
import androidx.annotation.FractionRes
import com.droibit.looking2.core.R

class ShapeAwareContentPadding(context: Context) {

    val firstItemTopPx: Int
    val lastItemBottomPx: Int
    val leftPx: Int
    val rightPx: Int

    init {
        val res = context.resources
        if (res.getBoolean(R.bool.round_shaped_screen)) {
            val screenPx = Size(res.displayMetrics.widthPixels, res.displayMetrics.heightPixels)
            firstItemTopPx = res.fractionOfScreenPx(
                screenPx.height,
                R.fraction.action_item_first_item_top_padding
            )
            lastItemBottomPx = res.fractionOfScreenPx(
                screenPx.height,
                R.fraction.action_item_last_item_bottom_padding
            )
            leftPx = res.fractionOfScreenPx(screenPx.width, R.fraction.action_item_left_padding)
            rightPx = res.fractionOfScreenPx(screenPx.width, R.fraction.action_item_right_padding)
        } else {
            firstItemTopPx = res.getDimensionPixelSize(
                R.dimen.square_action_item_first_item_top_padding
            )
            lastItemBottomPx = res.getDimensionPixelSize(
                R.dimen.square_action_item_last_item_bottom_padding
            )
            leftPx = res.getDimensionPixelSize(R.dimen.square_action_item_left_padding)
            rightPx = res.getDimensionPixelSize(R.dimen.square_action_item_right_padding)
        }
    }

    private fun Resources.fractionOfScreenPx(screenPx: Int, @FractionRes resId: Int): Int {
        val marginPercent = getFraction(resId, 1, 1)
        return (marginPercent * screenPx).toInt()
    }
}