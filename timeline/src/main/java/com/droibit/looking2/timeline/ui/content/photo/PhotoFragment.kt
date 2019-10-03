package com.droibit.looking2.timeline.ui.content.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import androidx.wear.widget.SwipeDismissFrameLayout
import com.droibit.looking2.timeline.databinding.FragmentPhotoBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class PhotoFragment : DaggerFragment() {

    val args: PhotoFragmentArgs by navArgs()

    @Inject
    lateinit var photoListAdapter: PhotoListAdapter

    private lateinit var binding: FragmentPhotoBinding

    // FIXME: Black background remains when swiping.
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
        binding.viewPager.apply {
            this.adapter = photoListAdapter
            this.orientation = ViewPager2.ORIENTATION_VERTICAL
        }
    }

    override fun onDestroyView() {
        binding.swipeDismissLayout.removeCallback(swipeDismissCallback)
        super.onDestroyView()
    }
}