package com.droibit.looking2.tweet.ui.input.keyboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.droibit.looking2.tweet.R
import com.droibit.looking2.tweet.databinding.FragmentTweetKeyboardBinding
import com.droibit.looking2.tweet.ui.TweetHostViewModel
import com.droibit.looking2.tweet.ui.input.TweetViewModel
import com.droibit.looking2.ui.common.Activities.Confirmation.SuccessIntent as SuccessConfirmationIntent
import com.droibit.looking2.ui.common.ext.observeEvent
import com.droibit.looking2.ui.common.view.ShapeAwareContentPadding
import com.droibit.looking2.ui.common.widget.PopBackSwipeDismissCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class KeyboardTweetFragment : Fragment() {

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    @Inject
    lateinit var contentPadding: ShapeAwareContentPadding

    private val viewModel: TweetViewModel by viewModels()

    private val hostViewModel: TweetHostViewModel by hiltNavGraphViewModels(R.id.navGraphTweet)

    private var _binding: FragmentTweetKeyboardBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTweetKeyboardBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.strings = hostViewModel.layoutString
            it.contentPadding = contentPadding
            it.viewModel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeDismissLayout.addCallback(swipeDismissCallback)

        observeTweetCompleted()
    }

    private fun observeTweetCompleted() {
        viewModel.tweetCompleted.observeEvent(viewLifecycleOwner) {
            val intent = SuccessConfirmationIntent(requireContext(), messageResId = null)
            startActivity(intent)
            findNavController().popBackStack(R.id.navGraphTweet, inclusive = true)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
