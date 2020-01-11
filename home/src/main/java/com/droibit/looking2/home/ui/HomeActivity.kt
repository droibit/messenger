package com.droibit.looking2.home.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.droibit.looking2.core.ui.widget.ActionItemListAdapter
import com.droibit.looking2.core.ui.widget.ActionItemListAdapter.ActionItem
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.core.util.analytics.sendScreenView
import com.droibit.looking2.home.databinding.ActivityHomeBinding
import com.droibit.looking2.home.ui.HomeNavigation.ACCOUNTS
import com.droibit.looking2.home.ui.HomeNavigation.LISTS
import com.droibit.looking2.home.ui.HomeNavigation.MENTIONS
import com.droibit.looking2.home.ui.HomeNavigation.SETTINGS
import com.droibit.looking2.home.ui.HomeNavigation.TIMELINE
import com.droibit.looking2.home.ui.HomeNavigation.TWEET
import javax.inject.Inject
import com.droibit.looking2.core.R as coreR
import com.droibit.looking2.home.R as homeR
import com.droibit.looking2.ui.Activities.Account as AccountActivity
import com.droibit.looking2.ui.Activities.Settings as SettingsActivity
import com.droibit.looking2.ui.Activities.Timeline as TimelineActivity
import com.droibit.looking2.ui.Activities.Tweet as TweetActivity

class HomeActivity : FragmentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var analytics: AnalyticsHelper

    @Inject
    lateinit var actionItemListAdapter: ActionItemListAdapter

    private lateinit var binding: ActivityHomeBinding

    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, homeR.layout.activity_home)
        binding.navigationList.apply {
            adapter = actionItemListAdapter
        }

        viewModel.activeAccountName.observe(this) {
            actionItemListAdapter.title = getString(coreR.string.twitter_account_name_with_at, it)
            (binding.navigationList.layoutManager as LinearLayoutManager)
                .scrollToPositionWithOffset(0, 0)
        }
    }

    override fun onResume() {
        super.onResume()
        analytics.sendScreenView(homeR.string.home_nav_label_home, this)
    }

    fun onActionItemClick(item: ActionItem) {
        val intent = when (HomeNavigation(item.id)) {
            TWEET -> TweetActivity.createIntent()
            TIMELINE -> TimelineActivity.createHomeIntent()
            MENTIONS -> TimelineActivity.createMentionsIntent()
            LISTS -> TimelineActivity.createListsIntent()
            ACCOUNTS -> AccountActivity.createIntent(needTwitterSignIn = false)
            SETTINGS -> SettingsActivity.createIntent()
        }
        startActivity(intent)
    }
}
