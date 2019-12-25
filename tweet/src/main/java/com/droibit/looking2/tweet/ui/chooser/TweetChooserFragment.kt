package com.droibit.looking2.tweet.ui.chooser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.tweet.databinding.FragmentTweetChooserBinding
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
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragment = this
        binding.screenTitle = title
    }

    @Suppress("UNUSED_PARAMETER")
    fun onKeyboardTweetButtonClick(v: View) {
        val direction = TweetChooserFragmentDirections.showKeyboardTweet()
        findNavController().navigate(direction)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onVoiceTweetButtonClick(v: View) {
        val direction = TweetChooserFragmentDirections.showVoiceTweet()
        findNavController().navigate(direction)
    }
}