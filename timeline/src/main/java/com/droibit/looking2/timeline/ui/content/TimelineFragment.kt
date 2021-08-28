package com.droibit.looking2.timeline.ui.content

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.remote.interactions.RemoteActivityHelper
import androidx.wear.widget.SwipeDismissFrameLayout
import app.cash.exhaustive.Exhaustive
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.ui.widget.PopBackSwipeDismissCallback
import com.droibit.looking2.core.util.ext.add
import com.droibit.looking2.core.util.ext.addCallback
import com.droibit.looking2.core.util.ext.navigateSafely
import com.droibit.looking2.core.util.ext.observeEvent
import com.droibit.looking2.core.util.ext.showToast
import com.droibit.looking2.timeline.databinding.FragmentTimelineBinding
import com.droibit.looking2.timeline.ui.content.TimelineFragmentDirections.Companion.toPhotos
import com.droibit.looking2.timeline.ui.widget.ListDividerItemDecoration
import com.droibit.looking2.ui.Activities.Confirmation.OpenOnPhoneIntent
import com.droibit.looking2.ui.Activities.Tweet.ReplyTweet
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.droibit.looking2.timeline.ui.content.TweetActionItemList.Item as TweetActionItem
import com.droibit.looking2.ui.Activities.Confirmation.SuccessIntent as SuccessConfirmationIntent
import com.droibit.looking2.ui.Activities.Tweet as TweetActivity

class TimelineFragment : DaggerFragment(), MenuItem.OnMenuItemClickListener {

    val args: TimelineFragmentArgs by navArgs()

    @Inject
    lateinit var tweetListAdapter: TweetListAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var tweetActionMenu: Menu

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    @Inject
    lateinit var remoteActivityHelper: RemoteActivityHelper

    private val timelineViewModel: TimelineViewModel by viewModels { viewModelFactory }

    private val tweetActionViewModel: TweetActionViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val tweetActionList: RecyclerView?
        get() = binding.tweetActionDrawer.getChildAt(0) as? RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = timelineViewModel
        }

        val backStackEntryCount = parentFragmentManager.backStackEntryCount
        return if (backStackEntryCount == 0) binding.root else {
            Timber.d("Wrapped SwipeDismissFrameLayout(backStackEntryCount=$backStackEntryCount)")
            SwipeDismissFrameLayout(requireContext()).apply {
                addView(binding.root)
                addCallback(viewLifecycleOwner, swipeDismissCallback)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.list.apply {
            this.addItemDecoration(ListDividerItemDecoration(requireContext()))
            this.adapter = tweetListAdapter
        }

        binding.tweetActionDrawer.also {
            it.setOnMenuItemClickListener(this)
        }

        observeReply()
        observePhotoList()
        observeTweetActionItemList()
        observeGetTimelineResult()
        observeRetweetCompleted()
        observeLikesCompleted()
        observeOpenTweetOnPhone()
    }

    private fun observeGetTimelineResult() {
        timelineViewModel.timeline.observe(viewLifecycleOwner) {
            showTimeline(it)
        }

        timelineViewModel.error.observe(viewLifecycleOwner) {
            it.consume()?.let(::showGetTimelineError)
        }
    }

    private fun showTimeline(timeline: List<Tweet>) {
        tweetListAdapter.setTweets(timeline)
    }

    private fun showGetTimelineError(error: GetTimelineErrorMessage) {
        @Exhaustive
        when (error) {
            is GetTimelineErrorMessage.Toast -> showToast(error)
        }
        requireActivity().finish()
    }

    private fun observeTweetActionItemList() {
        tweetActionViewModel.tweetActionItemList
            .observeEvent(viewLifecycleOwner) { (_, actionItems) ->
                val actionDrawerMenu = binding.tweetActionDrawer.menu
                actionDrawerMenu.clear()
                actionItems
                    .map { tweetActionMenu.findItem(it.id) }
                    .forEach { actionDrawerMenu.add(it) }

                tweetActionList?.layoutManager?.let {
                    (it as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
                }
                binding.tweetActionDrawer.controller.openDrawer()
            }
    }

    private fun observePhotoList() {
        tweetActionViewModel.photos.observeEvent(viewLifecycleOwner) { urls ->
            findNavController().navigateSafely(toPhotos(urls.toTypedArray()))
        }
    }

    private fun observeReply() {
        tweetActionViewModel.reply.observeEvent(viewLifecycleOwner) {
            val intent = TweetActivity.createIntent(ReplyTweet(it.id, it.user))
            startActivity(intent)
        }
    }

    private fun observeRetweetCompleted() {
        tweetActionViewModel.retweetCompleted.observeEvent(viewLifecycleOwner) {
            val intent = SuccessConfirmationIntent(requireContext(), messageResId = null)
            startActivity(intent)
        }
    }

    private fun observeLikesCompleted() {
        tweetActionViewModel.likesCompleted.observeEvent(viewLifecycleOwner) {
            val intent = SuccessConfirmationIntent(requireContext(), messageResId = null)
            startActivity(intent)
        }
    }

    private fun observeOpenTweetOnPhone() {
        tweetActionViewModel.openTweetOnPhone.observeEvent(viewLifecycleOwner) { url ->
            startActivity(OpenOnPhoneIntent(requireContext()))

            lifecycleScope.launch {
                remoteActivityHelper.startRemoteActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        .addCategory(Intent.CATEGORY_BROWSABLE)
                ).await()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        tweetActionViewModel.onTweetActionItemClick(
            actionItem = TweetActionItem(item.itemId)
        )
        binding.tweetActionDrawer.controller.closeDrawer()
        return true
    }

    fun onTweetClick(tweet: Tweet) {
        Timber.d("onTweetClick(${tweet.tweetUrl})")
        tweetActionViewModel.onTweetClick(tweet)
    }
}
