package com.droibit.looking2.account.ui.twitter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.droibit.looking2.account.R
import com.droibit.looking2.account.databinding.FragmentTwitterAccountListBinding
import com.droibit.looking2.account.ui.twitter.TwitterAccountListFragmentDirections.Companion.toConfirmTwitterSignOut
import com.droibit.looking2.account.ui.twitter.TwitterAccountListFragmentDirections.Companion.toTwitterSignIn
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.ui.view.OnRotaryScrollListener
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.core.util.ext.observeEvent
import com.droibit.looking2.core.util.ext.showToast
import dagger.android.support.DaggerFragment
import javax.inject.Inject
import com.droibit.looking2.ui.Activities.Account as AccountActivity

class TwitterAccountListFragment : DaggerFragment(), MenuItem.OnMenuItemClickListener {

    @Inject
    lateinit var contentPadding: ShapeAwareContentPadding

    @Inject
    lateinit var accountListAdapter: TwitterAccountListAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: TwitterAccountListViewModel by navGraphViewModels(R.id.navigationTwitterAccountList) { viewModelFactory }

    private var _binding: FragmentTwitterAccountListBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTwitterAccountListBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = viewModel
            it.contentPadding = contentPadding
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.accountActionDrawer.also {
            it.setOnMenuItemClickListener(this)
        }
        binding.accountContainer.setOnGenericMotionListener(OnRotaryScrollListener())

        binding.list.apply {
            this.setHasFixedSize(true)
            this.adapter = accountListAdapter
        }

        observeTwitterAccounts()
        observeShowSignOutConfirmation()
        observeRestartAppTiming()
        observeSignInTwitter()
    }

    private fun observeTwitterAccounts() {
        viewModel.twitterAccounts.observe(viewLifecycleOwner) {
            accountListAdapter.setAccounts(it)
        }
    }

    private fun observeShowSignOutConfirmation() {
        viewModel.showSignOutConfirmation.observeEvent(viewLifecycleOwner) {
            findNavController().navigate(toConfirmTwitterSignOut(account = it))
        }
    }

    private fun observeRestartAppTiming() {
        viewModel.restartAppTiming.observeEvent(viewLifecycleOwner) {
            val intent = AccountActivity.createRestartIntent()
            requireActivity().startActivity(intent)
        }
    }

    private fun observeSignInTwitter() {
        viewModel.signTwitter.observeEvent(viewLifecycleOwner) {
            findNavController().navigate(toTwitterSignIn())
        }

        viewModel.limitSignInTwitterErrorMessage.observeEvent(viewLifecycleOwner) {
            showToast(it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val action =
            TwitterAccountAction(item.itemId)
        viewModel.onAccountActionItemClick(action)
        binding.accountActionDrawer.controller.closeDrawer()
        return true
    }

    @UiThread
    fun onAccountItemClick(account: TwitterAccount) {
        viewModel.onAccountItemClick(account)
        binding.accountActionDrawer.controller.openDrawer()
    }
}