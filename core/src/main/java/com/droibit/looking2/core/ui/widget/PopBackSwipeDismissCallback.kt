package com.droibit.looking2.core.ui.widget

import androidx.core.view.isInvisible
import androidx.navigation.findNavController
import androidx.wear.widget.SwipeDismissFrameLayout
import javax.inject.Inject

class PopBackSwipeDismissCallback @Inject constructor() : SwipeDismissFrameLayout.Callback() {

    override fun onDismissed(layout: SwipeDismissFrameLayout) {
        // Prevent flicker on screen.
        layout.isInvisible = true
        layout.findNavController().popBackStack()
    }
}
