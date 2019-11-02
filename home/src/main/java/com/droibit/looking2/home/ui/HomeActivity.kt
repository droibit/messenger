package com.droibit.looking2.home.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.droibit.looking2.core.ui.widget.ActionItemListAdapter
import com.droibit.looking2.core.ui.widget.ActionItemListAdapter.ActionItem
import com.droibit.looking2.home.R
import com.droibit.looking2.home.databinding.ActivityHomeBinding
import com.droibit.looking2.home.ui.HomeNavigation.LISTS
import com.droibit.looking2.home.ui.HomeNavigation.MENTIONS
import com.droibit.looking2.home.ui.HomeNavigation.SETTINGS
import com.droibit.looking2.home.ui.HomeNavigation.TIMELINE
import com.droibit.looking2.home.ui.HomeNavigation.TWEET
import com.droibit.looking2.ui.Activities.Tweet as TweetActivity
import com.droibit.looking2.ui.Activities.Timeline as TimelineActivity
import com.droibit.looking2.ui.Activities.Settings as SettingsActivity
import javax.inject.Inject

class HomeActivity : FragmentActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var actionItemListAdapter: ActionItemListAdapter

    private lateinit var binding: ActivityHomeBinding

    private val viewModel: HomeViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.navigationList.apply {
            adapter = actionItemListAdapter
        }

        viewModel.activeAccountName.observe(this) {
            actionItemListAdapter.title = it
        }

        lifecycle.addObserver(viewModel)
    }

    fun onActionItemClick(item: ActionItem) {
        when (HomeNavigation.of(item.id)) {
            TWEET -> startActivity(TweetActivity.createIntent())
            TIMELINE -> startActivity(TimelineActivity.createHomeIntent())
            MENTIONS -> startActivity(TimelineActivity.createMentionsIntent())
            LISTS -> startActivity(TimelineActivity.createListsIntent())
            SETTINGS -> startActivity(SettingsActivity.createIntent())
            else -> Unit
        }
    }
}
