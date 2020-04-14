package com.droibit.looking2.account.ui.twitter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.droibit.looking2.account.databinding.FragmentTwitterAccountListBinding
import com.droibit.looking2.account.ui.twitter.TwitterAccountListFragmentDirections.Companion.toConfirmTwitterSignOut
import com.droibit.looking2.account.ui.twitter.TwitterAccountListFragmentDirections.Companion.toTwitterSignIn
import com.droibit.looking2.account.ui.twitter.signout.SignOutConfirmationDialogResult
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.ui.view.OnRotaryScrollListener
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.core.util.ext.consume
import com.droibit.looking2.core.util.ext.navigateSafely
import com.droibit.looking2.core.util.ext.observeEvent
import com.droibit.looking2.core.util.ext.requireCurrentBackStackEntry
import com.droibit.looking2.core.util.ext.showToast
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject
import com.droibit.looking2.ui.Activities.Account as AccountActivity

private const val RESULT_KEY_SIGN_OUT_CONFORMATION = "RESULT_KEY_SIGN_OUT_CONFORMATION"

class TwitterAccountListFragment : DaggerFragment(), MenuItem.OnMenuItemClickListener {

    @Inject
    lateinit var contentPadding: ShapeAwareContentPadding

    @Inject
    lateinit var accountListAdapter: TwitterAccountListAdapter

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: TwitterAccountListViewModel by viewModels { viewModelFactory }

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
        observeSignOutConformationResult()
        observeRestartApp()
        observeSignInTwitter()
    }

    private fun observeTwitterAccounts() {
        viewModel.twitterAccounts.observe(viewLifecycleOwner) {
            accountListAdapter.setAccounts(it)
        }
    }

    private fun observeShowSignOutConfirmation() {
        viewModel.showSignOutConfirmation.observeEvent(viewLifecycleOwner) {
            findNavController().navigateSafely(
                toConfirmTwitterSignOut(
                    resultKey = RESULT_KEY_SIGN_OUT_CONFORMATION,
                    account = it
                )
            )
        }
    }

    private fun observeSignOutConformationResult() {
        val navBackStackEntry = findNavController().requireCurrentBackStackEntry()
        navBackStackEntry.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                navBackStackEntry.savedStateHandle.consume<SignOutConfirmationDialogResult>(
                    RESULT_KEY_SIGN_OUT_CONFORMATION
                )?.let {
                    viewModel.onSignOutConfirmationDialogResult(it)
                    Timber.d("Sign out confirmation result: $it")
                }
            }
        })
    }

    private fun observeRestartApp() {
        viewModel.restartApp.observeEvent(viewLifecycleOwner) {
            val intent = AccountActivity.createRestartIntent()
            requireActivity().startActivity(intent)
        }
    }

    private fun observeSignInTwitter() {
        viewModel.signTwitter.observeEvent(viewLifecycleOwner) {
            findNavController().navigateSafely(toTwitterSignIn())
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
        val action = TwitterAccountAction(item.itemId)
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