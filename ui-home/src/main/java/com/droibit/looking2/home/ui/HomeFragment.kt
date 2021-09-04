package com.droibit.looking2.home.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.droibit.looking2.home.R
import com.droibit.looking2.home.databinding.FragmentHomeBinding
import com.droibit.looking2.ui.common.R as commonR
import com.droibit.looking2.ui.common.navigation.DeepLinkDirections.toAccounts
import com.droibit.looking2.ui.common.navigation.DeepLinkDirections.toHomeTimeline
import com.droibit.looking2.ui.common.navigation.DeepLinkDirections.toMentionsTimeline
import com.droibit.looking2.ui.common.navigation.DeepLinkDirections.toMyLists
import com.droibit.looking2.ui.common.navigation.DeepLinkDirections.toSettings
import com.droibit.looking2.ui.common.navigation.DeepLinkDirections.toTweet
import com.droibit.looking2.ui.common.navigation.default
import com.droibit.looking2.ui.common.widget.ActionItemListAdapter
import com.droibit.looking2.ui.common.widget.ActionItemListAdapter.ActionItem
import com.droibit.looking2.ui.common.widget.OnActionItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeFragment : Fragment(), OnActionItemClickListener {

    @Named("home")
    @Inject
    lateinit var actionItemListAdapter: ActionItemListAdapter

    private val viewModel: HomeViewModel by viewModels()

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = requireNotNull(_binding)

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
            actionItemListAdapter.title = getString(commonR.string.twitter_account_name_with_at, it)

            binding.navigationList.apply {
                (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
                requestFocus()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onActionItemClick(item: ActionItem) {
        val toNext = when (HomeNavigation(item.id)) {
            HomeNavigation.TWEET -> toTweet()
            HomeNavigation.TIMELINE -> toHomeTimeline()
            HomeNavigation.MENTIONS -> toMentionsTimeline()
            HomeNavigation.LISTS -> toMyLists()
            HomeNavigation.ACCOUNTS -> toAccounts()
            HomeNavigation.SETTINGS -> toSettings()
        }

        with(findNavController()) {
            if (currentBackStackEntry?.destination?.id == R.id.homeFragment) {
                navigate(
                    toNext,
                    navOptions {
                        anim { default() }
                    }
                )
            }
        }
    }
}
