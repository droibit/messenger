package com.droibit.looking2.tweet.ui.input.keyboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.core.ui.widget.PopBackSwipeDismissCallback
import com.droibit.looking2.core.util.ext.observeEvent
import com.droibit.looking2.tweet.databinding.FragmentTweetKeyboardBinding
import com.droibit.looking2.tweet.ui.input.TweetLayoutString
import com.droibit.looking2.tweet.ui.input.TweetViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import com.droibit.looking2.ui.Activities.Confirmation.SuccessIntent as SuccessConfirmationIntent

class KeyboardTweetFragment : DaggerFragment() {

    @Inject
    lateinit var layoutString: TweetLayoutString

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    @Inject
    lateinit var contentPadding: ShapeAwareContentPadding

    private val viewModel: TweetViewModel by viewModels { viewModelFactory }

    private lateinit var binding: FragmentTweetKeyboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTweetKeyboardBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.strings = layoutString
            it.contentPadding = contentPadding
            it.viewModel = viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeDismissLayout.addCallback(swipeDismissCallback)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeTweetCompleted()
    }

    private fun observeTweetCompleted() {
        viewModel.tweetCompleted.observeEvent(viewLifecycleOwner) {
            val intent = SuccessConfirmationIntent(requireContext(), messageResId = null)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        binding.swipeDismissLayout.removeCallback(swipeDismissCallback)
        super.onDestroyView()
    }
}