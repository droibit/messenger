package com.droibit.looking2.tweet.ui.input.keyboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.wear.widget.SwipeDismissFrameLayout
import com.droibit.looking2.core.util.ext.showLongToast
import com.droibit.looking2.core.util.ext.showNetworkErrorToast
import com.droibit.looking2.core.util.ext.showShortToast
import com.droibit.looking2.tweet.databinding.FragmentTweetKeyboardBinding
import com.droibit.looking2.tweet.ui.input.SuccessfulMessage
import com.droibit.looking2.tweet.ui.input.TweetResult
import com.droibit.looking2.tweet.ui.input.TweetResult.FailureType
import com.droibit.looking2.tweet.ui.input.TweetViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import javax.inject.Named
import kotlin.LazyThreadSafetyMode.NONE

class KeyboardTweetFragment : DaggerFragment() {

    @Inject
    lateinit var layoutString: LayoutString

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: TweetViewModel by viewModels { viewModelFactory }

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

        binding.lifecycleOwner = viewLifecycleOwner
        binding.strings = layoutString
        binding.viewModel = viewModel
        binding.swipeDismissLayout.addCallback(swipeDismissCallback)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeTweetResult()
    }

    private fun observeTweetResult() {
        viewModel.tweetResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is TweetResult.Success -> {
                    result.message.consume()?.let(this::showTweetSuccessful)
                }
                is TweetResult.Failure -> {
                    result.type.consume()?.let(this::showTweetFailure)
                }
            }
            binding.showProgress = result is TweetResult.InProgress
        }
    }

    private fun showTweetSuccessful(message: SuccessfulMessage) {
        showShortToast(message.resId)
        requireActivity().finish()
    }

    private fun showTweetFailure(failureType: FailureType) {
        when (failureType) {
            is FailureType.Network -> showNetworkErrorToast()
            is FailureType.UnExpected -> showLongToast(failureType.messageResId)
        }
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