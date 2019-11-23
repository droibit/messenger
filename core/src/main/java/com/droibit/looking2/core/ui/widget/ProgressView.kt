package com.droibit.looking2.core.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import com.droibit.looking2.core.R

class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        orientation = VERTICAL
        gravity = Gravity.CENTER
        View.inflate(context, R.layout.view_progress, this)

        context.withStyledAttributes(attrs, R.styleable.ProgressView, defStyleAttr) {
            val textView = findViewById<TextView>(R.id.progressText)
            textView.text = getString(R.styleable.ProgressView_android_text)
        }
    }
}