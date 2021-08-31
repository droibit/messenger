package com.droibit.looking2.timeline.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.droibit.looking2.timeline.R

class EmptyContentView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_empty_content, this)
        context.withStyledAttributes(attrs, R.styleable.EmptyContentView) {
            val textView = findViewById<TextView>(R.id.text)
            textView.text = getString(R.styleable.EmptyContentView_android_text)
        }
    }
}
