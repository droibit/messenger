package com.droibit.looking2.ui.common.widget

import androidx.core.view.isInvisible
import androidx.navigation.findNavController
import androidx.wear.widget.SwipeDismissFrameLayout
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class PopBackSwipeDismissCallback @Inject constructor() : SwipeDismissFrameLayout.Callback() {

    override fun onDismissed(layout: SwipeDismissFrameLayout) {
        // Prevent flicker on screen.
        layout.isInvisible = true
        layout.findNavController().popBackStack()
    }
}
