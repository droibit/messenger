package com.droibit.looking2.timeline.ui.content

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.wear.widget.SwipeDismissFrameLayout
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.timeline.databinding.FragmentTimelineBinding
import com.droibit.looking2.timeline.ui.content.TweetListAdapter.Companion.TAG_TWEET_PHOTO
import com.squareup.picasso.Picasso
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class TimelineFragment : Fragment() {

    val args: TimelineFragmentArgs by navArgs()

    @Inject
    lateinit var tweetListAdapter: TweetListAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val timelineViewModel: TimelineViewModel by viewModels { viewModelFactory }

    private lateinit var binding: FragmentTimelineBinding

    private val swipeDismissCallback: SwipeDismissFrameLayout.Callback by lazy(NONE) {
        object : SwipeDismissFrameLayout.Callback() {
            override fun onDismissed(layout: SwipeDismissFrameLayout) {
                // Prevent flicker on screen.
                layout.isInvisible = true
                findNavController().popBackStack()
            }
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimelineBinding.inflate(inflater, container, false)

        val backStackEntryCount = requireFragmentManager().backStackEntryCount
        return if (backStackEntryCount == 0) binding.root else {
            Timber.d("Wrapped SwipeDismissFrameLayout(backStackEntryCount=$backStackEntryCount)")
            SwipeDismissFrameLayout(context).apply {
                addView(binding.root)
                addCallback(swipeDismissCallback)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.list.apply {
            this.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            this.adapter = tweetListAdapter
        }
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        timelineViewModel.getTimelineResult.observe(viewLifecycleOwner) {
            TODO()
        }

        lifecycle.addObserver(timelineViewModel)
    }

    override fun onDestroyView() {
        (view as? SwipeDismissFrameLayout)?.removeCallback(swipeDismissCallback)
        Picasso.get().cancelTag(TAG_TWEET_PHOTO)
        super.onDestroyView()
    }

    fun onTweetClick(tweet: Tweet) {
        Timber.d("onTweetClick(${tweet.url})")
    }
}