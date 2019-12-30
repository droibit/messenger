package com.droibit.looking2.tweet.ui.chooser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.tweet.databinding.FragmentTweetChooserBinding
import com.droibit.looking2.tweet.ui.chooser.TweetChooserFragmentDirections.Companion.toKeyboardTweet
import com.droibit.looking2.tweet.ui.chooser.TweetChooserFragmentDirections.Companion.toVoiceTweet
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import javax.inject.Named

class TweetChooserFragment : DaggerFragment() {

    @Inject
    @Named("title")
    lateinit var title: String

    @Inject
    lateinit var contentPadding: ShapeAwareContentPadding

    private lateinit var binding: FragmentTweetChooserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTweetChooserBinding.inflate(inflater, container, false).also {
            it.contentPadding = contentPadding
            it.fragment = this
            it.screenTitle = title
        }
        return binding.root
    }

    @Suppress("UNUSED_PARAMETER")
    fun onKeyboardTweetButtonClick(v: View) {
        findNavController().navigate(toKeyboardTweet())
    }

    @Suppress("UNUSED_PARAMETER")
    fun onVoiceTweetButtonClick(v: View) {
        findNavController().navigate(toVoiceTweet())
    }
}