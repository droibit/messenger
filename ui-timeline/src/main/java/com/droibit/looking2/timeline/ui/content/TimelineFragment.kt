package com.droibit.looking2.timeline.ui.content

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.remote.interactions.RemoteActivityHelper
import androidx.wear.widget.SwipeDismissFrameLayout
import app.cash.exhaustive.Exhaustive
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.util.ext.add
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.databinding.FragmentTimelineBinding
import com.droibit.looking2.timeline.ui.content.TimelineFragmentDirections.Companion.toPhotos
import com.droibit.looking2.timeline.ui.content.TweetActionItemList.Item as TweetActionItem
import com.droibit.looking2.timeline.ui.widget.ListDividerItemDecoration
import com.droibit.looking2.ui.common.Activities.Confirmation.OpenOnPhoneIntent
import com.droibit.looking2.ui.common.Activities.Confirmation.SuccessIntent as SuccessConfirmationIntent
import com.droibit.looking2.ui.common.ext.addCallback
import com.droibit.looking2.ui.common.ext.navigateSafely
import com.droibit.looking2.ui.common.ext.observeEvent
import com.droibit.looking2.ui.common.ext.showToast
import com.droibit.looking2.ui.common.navigation.DeepLinkDirections.toTweet
import com.droibit.looking2.ui.common.navigation.default
import com.droibit.looking2.ui.common.widget.PopBackSwipeDismissCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class TimelineFragment :
    Fragment(),
    TweetListAdapter.OnItemClickListener,
    MenuItem.OnMenuItemClickListener {

    val args: TimelineFragmentArgs by navArgs()

    @Inject
    lateinit var tweetListAdapter: TweetListAdapter

    @Inject
    lateinit var tweetActionMenu: Menu

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    @Inject
    lateinit var remoteActivityHelper: RemoteActivityHelper

    private val timelineViewModel: TimelineViewModel by viewModels()

    private val tweetActionViewModel: TweetActionViewModel by viewModels()

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val tweetActionList: RecyclerView?
        get() {
            return binding.tweetActionDrawer.children
                .firstOrNull { it is RecyclerView } as? RecyclerView
        }

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
        tweetListAdapter.submitList(timeline)
    }

    private fun showGetTimelineError(error: GetTimelineErrorMessage) {
        @Exhaustive
        when (error) {
            is GetTimelineErrorMessage.Toast -> showToast(error)
        }
        findNavController().popBackStack()
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
            with(findNavController()) {
                if (currentBackStackEntry?.destination?.id == R.id.timelineFragment) {
                    navigate(
                        toTweet(replyTweet = it),
                        navOptions {
                            anim { default() }
                        }
                    )
                }
            }
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
                try {
                    remoteActivityHelper.startRemoteActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            .addCategory(Intent.CATEGORY_BROWSABLE)
                    ).await()
                } catch (e: Exception) {
                    Timber.d(e)
                }
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

    override fun onTweetClick(tweet: Tweet) {
        Timber.d("onTweetClick(${tweet.tweetUrl})")
        tweetActionViewModel.onTweetClick(tweet)
    }
}