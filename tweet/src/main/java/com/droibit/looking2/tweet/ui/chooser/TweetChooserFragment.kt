package com.droibit.looking2.tweet.ui.chooser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.droibit.looking2.tweet.databinding.FragmentTweetChooserBinding
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import javax.inject.Named

class TweetChooserFragment : DaggerFragment() {

    @Inject
    @Named("title")
    lateinit var title: String

    private lateinit var binding: FragmentTweetChooserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTweetChooserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.screenTitle = title
    }
}