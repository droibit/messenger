package com.droibit.looking2.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.droibit.looking2.core.R as coreR
import com.droibit.looking2.core.ui.Activities
import com.droibit.looking2.core.ui.widget.ActionItemListAdapter
import com.droibit.looking2.core.ui.widget.ActionItemListAdapter.ActionItem
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.home.R
import com.droibit.looking2.home.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    @Inject
    lateinit var analytics: AnalyticsHelper

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var actionItemListAdapter: ActionItemListAdapter

    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navigationList.apply {
            adapter = actionItemListAdapter
        }

        viewModel.activeAccountName.observe(viewLifecycleOwner) {
            actionItemListAdapter.title = getString(coreR.string.twitter_account_name_with_at, it)

            binding.navigationList.apply {
                (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
                requestFocus()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        analytics.sendScreenView(
            screenName = getString(R.string.home_nav_label_home),
            screenClass = null
        )
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun onActionItemClick(item: ActionItem) {
        val intent = when (HomeNavigation(item.id)) {
            HomeNavigation.TWEET -> Activities.Tweet.createIntent()
            HomeNavigation.TIMELINE -> Activities.Timeline.createHomeIntent()
            HomeNavigation.MENTIONS -> Activities.Timeline.createMentionsIntent()
            HomeNavigation.LISTS -> Activities.Timeline.createListsIntent()
            HomeNavigation.ACCOUNTS -> Activities.Account.createIntent(needTwitterSignIn = false)
            HomeNavigation.SETTINGS -> Activities.Settings.createIntent()
        }
        startActivity(intent)
    }
}
