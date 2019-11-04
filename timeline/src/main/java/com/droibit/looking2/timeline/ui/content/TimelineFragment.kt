package com.droibit.looking2.timeline.ui.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.SwipeDismissFrameLayout
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.util.ext.exhaustive
import com.droibit.looking2.core.util.ext.observeIfNotConsumed
import com.droibit.looking2.core.util.ext.showNetworkErrorToast
import com.droibit.looking2.core.util.ext.showRateLimitingErrorToast
import com.droibit.looking2.core.util.ext.showShortToast
import com.droibit.looking2.timeline.databinding.FragmentTimelineBinding
import com.droibit.looking2.timeline.ui.content.TweetListAdapter.Companion.TAG_TWEET_USER_ICON
import com.droibit.looking2.ui.Activities
import com.droibit.looking2.ui.Activities.Tweet.ReplyTweet
import com.droibit.looking2.ui.Activities.Tweet as TweetActivity
import com.squareup.picasso.Picasso
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE
import com.droibit.looking2.timeline.ui.content.GetTimelineResult.FailureType as GetTimelineFailureType

class TimelineFragment : DaggerFragment(), MenuItem.OnMenuItemClickListener {

    val args: TimelineFragmentArgs by navArgs()

    @Inject
    lateinit var tweetListAdapter: TweetListAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    internal lateinit var tweetActionMenu: Menu

    private val timelineViewModel: TimelineViewModel by viewModels { viewModelFactory }

    private val tweetActionViewModel: TweetActionViewModel by viewModels { viewModelFactory }

    private lateinit var binding: FragmentTimelineBinding

    private lateinit var tweetActionList: RecyclerView

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
        binding = FragmentTimelineBinding.inflate(inflater, container, false)

        val backStackEntryCount = requireFragmentManager().backStackEntryCount
        return if (backStackEntryCount == 0) binding.root else {
            Timber.d("Wrapped SwipeDismissFrameLayout(backStackEntryCount=$backStackEntryCount)")
            SwipeDismissFrameLayout(requireContext()).apply {
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

        binding.tweetActionDrawer.also {
            it.setOnMenuItemClickListener(this)
            // When displaying action list,
            // use RecyclerView to change the scroll position to top.
            this.tweetActionList = it.getChildAt(0) as RecyclerView
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeReply()
        observePhotoList()
        observeTweetAction()
        observeGetTimelineResult()

        lifecycle.addObserver(timelineViewModel)
    }

    private fun observeGetTimelineResult() {
        timelineViewModel.getTimelineResult.observe(viewLifecycleOwner) {
            when (it) {
                is GetTimelineResult.Success -> showTimeline(it.timeline)
                is GetTimelineResult.Failure -> showGetTimelineFailureResult(it.type)
            }
            binding.showProgress = it is GetTimelineResult.InProgress
        }
    }

    private fun showTimeline(timeline: List<Tweet>) {
        tweetListAdapter.setTweets(timeline)
        binding.showContent = timeline.isNotEmpty()
    }

    private fun showGetTimelineFailureResult(failureType: GetTimelineFailureType) {
        when (failureType) {
            is GetTimelineFailureType.Network -> showNetworkErrorToast()
            is GetTimelineFailureType.UnExpected -> showShortToast(failureType.messageResId)
            is GetTimelineFailureType.Limited -> showRateLimitingErrorToast()
        }.exhaustive
        requireActivity().finish()
    }

    private fun observeTweetAction() {
        tweetActionViewModel.tweetAction.observeIfNotConsumed(viewLifecycleOwner) { tweetAction ->
            val actionDrawerMenu = binding.tweetActionDrawer.menu
            actionDrawerMenu.clear()
            tweetAction.items
                .map { tweetActionMenu.findItem(it.id) }
                .forEach { actionMenuItem ->
                    actionDrawerMenu.add(
                        actionMenuItem.groupId,
                        actionMenuItem.itemId,
                        actionMenuItem.order,
                        actionMenuItem.title
                    ).also { it.icon = actionMenuItem.icon }
                }

            @Suppress("CAST_NEVER_SUCCEEDS")
            (tweetActionList.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(0, 0)
            binding.tweetActionDrawer.controller.openDrawer()
        }
    }

    private fun observePhotoList() {
        tweetActionViewModel.photos.observeIfNotConsumed(viewLifecycleOwner) {
            val directions = TimelineFragmentDirections.showPhotos(it.toTypedArray())
            findNavController().navigate(directions)
        }
    }

    private fun observeReply() {
        tweetActionViewModel.reply.observeIfNotConsumed(viewLifecycleOwner) {
            val intent = TweetActivity.createIntent(ReplyTweet(it.id, it.user))
            startActivity(intent)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        tweetActionViewModel.onTweetActionItemClick(
            actionItem = TweetAction.Item.valueOf(item.itemId)
        )
        binding.tweetActionDrawer.controller.closeDrawer()
        return true
    }

    override fun onDestroyView() {
        (view as? SwipeDismissFrameLayout)?.removeCallback(swipeDismissCallback)
        Picasso.get().cancelTag(TAG_TWEET_USER_ICON)
        super.onDestroyView()
    }

    fun onTweetClick(tweet: Tweet) {
        Timber.d("onTweetClick(${tweet.url})")
        tweetActionViewModel.onTweetClick(tweet)
    }
}