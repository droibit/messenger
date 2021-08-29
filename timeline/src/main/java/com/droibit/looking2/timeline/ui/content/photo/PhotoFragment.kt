package com.droibit.looking2.timeline.ui.content.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.droibit.looking2.core.ui.widget.PopBackSwipeDismissCallback
import com.droibit.looking2.timeline.databinding.FragmentPhotoBinding
import dagger.android.support.DaggerFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhotoFragment : DaggerFragment() {

    val args: PhotoFragmentArgs by navArgs()

    @Inject
    lateinit var photoListAdapter: PhotoListAdapter

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    private var _binding: FragmentPhotoBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.apply {
            this.adapter = photoListAdapter
            this.orientation = ViewPager2.ORIENTATION_VERTICAL
        }
        binding.swipeDismissLayout.addCallback(swipeDismissCallback)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
