package com.droibit.looking2.timeline.ui.content.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.droibit.looking2.timeline.databinding.FragmentPhotoBinding
import com.droibit.looking2.ui.common.widget.PopBackSwipeDismissCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PhotoFragment : Fragment() {

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    @Inject
    lateinit var photoListAdapter: PhotoListAdapter

    private val args: PhotoFragmentArgs by navArgs()

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
            this.orientation = ViewPager2.ORIENTATION_VERTICAL
            this.adapter = photoListAdapter.also {
                it.submitList(args.urls.toList())
            }
        }
        binding.swipeDismissLayout.addCallback(swipeDismissCallback)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
