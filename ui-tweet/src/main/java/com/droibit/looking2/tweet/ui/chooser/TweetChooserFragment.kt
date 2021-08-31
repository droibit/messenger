package com.droibit.looking2.tweet.ui.chooser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.droibit.looking2.tweet.databinding.FragmentTweetChooserBinding
import com.droibit.looking2.tweet.ui.chooser.TweetChooserFragmentDirections.Companion.toKeyboardTweet
import com.droibit.looking2.tweet.ui.chooser.TweetChooserFragmentDirections.Companion.toVoiceTweet
import com.droibit.looking2.ui.common.ext.navigateSafely
import com.droibit.looking2.ui.common.view.ShapeAwareContentPadding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class TweetChooserFragment : Fragment() {

    @Inject
    @Named("title")
    lateinit var title: String

    @Inject
    lateinit var contentPadding: ShapeAwareContentPadding

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
            it.screenTitle = title
        }
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onKeyboardTweetButtonClick(v: View) {
        findNavController().navigateSafely(toKeyboardTweet())
    }

    @Suppress("UNUSED_PARAMETER")
    fun onVoiceTweetButtonClick(v: View) {
        findNavController().navigateSafely(toVoiceTweet())
    }
}
