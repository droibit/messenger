package com.droibit.looking2.timeline.ui.content.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.wear.widget.SwipeDismissFrameLayout
import com.droibit.looking2.timeline.databinding.FragmentPhotoBinding
import dagger.android.support.DaggerFragment
import kotlin.LazyThreadSafetyMode.NONE

class PhotoFragment : DaggerFragment() {

    val args: PhotoFragmentArgs by navArgs()

    private lateinit var binding: FragmentPhotoBinding

    private val swipeDismissCallback: SwipeDismissFrameLayout.Callback by lazy(NONE) {
        object : SwipeDismissFrameLayout.Callback() {
            override fun onDismissed(layout: SwipeDismissFrameLayout) {
                // Prevent flicker on screen.
                layout.isInvisible = true
                findNavController().popBackStack()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeDismissLayout.addCallback(swipeDismissCallback)
    }

    override fun onDestroyView() {
        binding.swipeDismissLayout.removeCallback(swipeDismissCallback)
        super.onDestroyView()
    }
}