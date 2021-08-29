package com.droibit.looking2.account.ui.twitter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.droibit.looking2.account.databinding.FragmentTwitterAccountListBinding
import com.droibit.looking2.account.ui.twitter.TwitterAccountListFragmentDirections.Companion.toConfirmTwitterSignOut
import com.droibit.looking2.account.ui.twitter.TwitterAccountListFragmentDirections.Companion.toTwitterSignIn
import com.droibit.looking2.account.ui.twitter.signout.SignOutConfirmationDialogResult
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.ui.Activities.Account as AccountActivity
import com.droibit.looking2.core.ui.view.OnRotaryScrollListener
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.core.util.ext.navigateSafely
import com.droibit.looking2.core.util.ext.observeEvent
import com.droibit.looking2.core.util.ext.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import timber.log.Timber

private const val REQUEST_KEY_SIGN_OUT_CONFORMATION = "REQUEST_KEY_SIGN_OUT_CONFORMATION"

@AndroidEntryPoint
class TwitterAccountListFragment : Fragment(), MenuItem.OnMenuItemClickListener {

    @Inject
    lateinit var contentPadding: ShapeAwareContentPadding

    @Inject
    lateinit var accountListAdapter: TwitterAccountListAdapter

    private val viewModel: TwitterAccountListViewModel by viewModels()

    private var _binding: FragmentTwitterAccountListBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeSignOutConformationResult()
    }

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
                    requestKey = REQUEST_KEY_SIGN_OUT_CONFORMATION,
                    account = it
                )
            )
        }
    }

    private fun observeSignOutConformationResult() {
        setFragmentResultListener(REQUEST_KEY_SIGN_OUT_CONFORMATION) { _, data ->
            val result = SignOutConfirmationDialogResult(data)
            viewModel.onSignOutConfirmationDialogResult(result)
            Timber.d("Sign out confirmation result: $result")
        }
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
