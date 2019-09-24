package com.droibit.looking2.timeline.ui.content

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.wear.widget.SwipeDismissFrameLayout
import com.droibit.looking2.timeline.databinding.FragmentTimelineBinding
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import kotlin.LazyThreadSafetyMode.NONE

class TimelineFragment : Fragment() {

    val args: TimelineFragmentArgs by navArgs()

    private lateinit var binding: FragmentTimelineBinding

    private val swipeDismissCallback: SwipeDismissFrameLayout.Callback by lazy(NONE) {
        object : SwipeDismissFrameLayout.Callback() {
            override fun onDismissed(layout: SwipeDismissFrameLayout) {
                // Prevent flicker on screen.
                layout.isInvisible = true
                findNavController().popBackStack()
            }
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimelineBinding.inflate(inflater, container, false)

        val backStackEntryCount = requireFragmentManager().backStackEntryCount
        return if (backStackEntryCount == 0) binding.root else {
            Timber.d("Wrapped SwipeDismissFrameLayout(backStackEntryCount=$backStackEntryCount)")
            SwipeDismissFrameLayout(context).apply {
                addView(binding.root)
                addCallback(swipeDismissCallback)
            }
        }
    }

    override fun onDestroyView() {
        (view as? SwipeDismissFrameLayout)?.removeCallback(swipeDismissCallback)
        super.onDestroyView()
    }
}