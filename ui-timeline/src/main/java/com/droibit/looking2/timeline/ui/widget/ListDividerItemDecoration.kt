package com.droibit.looking2.timeline.ui.widget

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import com.droibit.looking2.timeline.R

internal class ListDividerItemDecoration(context: Context) :
    DividerItemDecoration(context, VERTICAL) {

    init {
        val divider = requireNotNull(ContextCompat.getDrawable(context, R.drawable.divider_list))
        setDrawable(divider)
    }
}
