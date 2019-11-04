package com.droibit.looking2.tweet.ui.input.keyboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.wear.widget.SwipeDismissFrameLayout
import com.droibit.looking2.tweet.databinding.FragmentTweetKeyboardBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import javax.inject.Named
import kotlin.LazyThreadSafetyMode.NONE

class KeyboardTweetFragment : DaggerFragment() {

    @Inject
    lateinit var layoutString: LayoutString

    private lateinit var binding: FragmentTweetKeyboardBinding

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
        binding = FragmentTweetKeyboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.strings = layoutString
        binding.lifecycleOwner = viewLifecycleOwner
        binding.swipeDismissLayout.addCallback(swipeDismissCallback)
    }

    override fun onDestroyView() {
        binding.swipeDismissLayout.removeCallback(swipeDismissCallback)
        super.onDestroyView()
    }

    class LayoutString @Inject constructor(
        @Named("title") val title: String,
        @Named("replyUser") val replyUser: String,
        @Named("tweetTextHint") val tweetTextHint: String
    )
}