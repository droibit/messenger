package com.droibit.looking2.timeline.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.droibit.looking2.ui.common.ext.navigateSafely

class TimelineTrampolineFragment : Fragment() {

    private val navArgs: TimelineTrampolineFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val destination = TimelineDestination(navArgs.id)
        findNavController().navigateSafely(destination.toDirections())
    }
}
