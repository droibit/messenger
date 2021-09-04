package com.droibit.looking2.tweet.ui.chooser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.fragment.findNavController
import com.droibit.looking2.tweet.R
import com.droibit.looking2.tweet.databinding.FragmentTweetChooserBinding
import com.droibit.looking2.tweet.ui.ReplyTweet
import com.droibit.looking2.tweet.ui.TweetHostViewModel
import com.droibit.looking2.tweet.ui.chooser.TweetChooserFragmentDirections.Companion.toKeyboardTweet
import com.droibit.looking2.tweet.ui.chooser.TweetChooserFragmentDirections.Companion.toVoiceTweet
import com.droibit.looking2.ui.common.ext.addCallback
import com.droibit.looking2.ui.common.ext.navigateSafely
import com.droibit.looking2.ui.common.view.ShapeAwareContentPadding
import com.droibit.looking2.ui.common.widget.PopBackSwipeDismissCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TweetChooserFragment : Fragment() {

    @Inject
    lateinit var contentPadding: ShapeAwareContentPadding

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    private val viewModel: TweetHostViewModel by hiltNavGraphViewModels(R.id.navGraphTweet)

    private var _binding: FragmentTweetChooserBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTweetChooserBinding.inflate(inflater, container, false).also {
            it.contentPadding = contentPadding
            it.fragment = this
            it.screenTitle = viewModel.layoutString.title
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeDismissLayout.addCallback(viewLifecycleOwner, swipeDismissCallback)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onKeyboardTweetButtonClick(v: View) {
        val replyTweet: ReplyTweet? = viewModel.replyTweet.orElse(null)
        findNavController().navigateSafely(toKeyboardTweet(replyTweet))
    }

    @Suppress("UNUSED_PARAMETER")
    fun onVoiceTweetButtonClick(v: View) {
        val replyTweet: ReplyTweet? = viewModel.replyTweet.orElse(null)
        findNavController().navigateSafely(toVoiceTweet(replyTweet))
    }
}
