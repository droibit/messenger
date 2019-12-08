package com.droibit.looking2.tweet.ui.input.keyboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.droibit.looking2.core.ui.widget.PopBackSwipeDismissCallback
import com.droibit.looking2.tweet.databinding.FragmentTweetKeyboardBinding
import com.droibit.looking2.tweet.ui.input.TweetLayoutString
import com.droibit.looking2.tweet.ui.input.TweetResult
import com.droibit.looking2.tweet.ui.input.TweetViewModel
import com.droibit.looking2.tweet.ui.input.showTweetFailure
import com.droibit.looking2.tweet.ui.input.showTweetSuccessful
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class KeyboardTweetFragment : DaggerFragment() {

    @Inject
    lateinit var layoutString: TweetLayoutString

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

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

        observeTweetResult()
    }

    private fun observeTweetResult() {
        viewModel.tweetResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is TweetResult.Success -> {
                    result.message.consume()?.let(::showTweetSuccessful)
                }
                is TweetResult.Failure -> {
                    result.type.consume()?.let(::showTweetFailure)
                }
            }
            binding.showProgress = result is TweetResult.InProgress
        }
    }

    override fun onDestroyView() {
        binding.swipeDismissLayout.removeCallback(swipeDismissCallback)
        super.onDestroyView()
    }
}