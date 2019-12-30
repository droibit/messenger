package com.droibit.looking2.account.ui.signin.twitter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.wear.widget.SwipeDismissFrameLayout
import com.droibit.looking2.account.R
import com.droibit.looking2.account.databinding.FragmentTwitterSigninBinding
import com.droibit.looking2.account.ui.signin.twitter.TwitterSignInFragmentDirections.Companion.toConfirmTwitterSignIn
import com.droibit.looking2.core.ui.widget.PopBackSwipeDismissCallback
import com.droibit.looking2.core.util.checker.PlayServicesChecker
import com.droibit.looking2.core.util.ext.addCallback
import com.droibit.looking2.core.util.ext.observeEvent
import com.droibit.looking2.core.util.ext.showNetworkErrorToast
import com.droibit.looking2.ui.Activities.Confirmation.FailureIntent
import com.droibit.looking2.ui.Activities.Confirmation.OpenOnPhoneIntent
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject
import com.droibit.looking2.ui.Activities.Home as HomeActivity

private const val REQUEST_CODE_RESOLVE_PLAY_SERVICES_ERROR = 1

class TwitterSignInFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var playServicesChecker: PlayServicesChecker

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    private lateinit var binding: FragmentTwitterSigninBinding

    private val signInViewModel: TwitterSignInViewModel by navGraphViewModels(R.id.navigationTwitter) {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTwitterSigninBinding.inflate(inflater, container, false).also {
            it.lifecycleOwner = viewLifecycleOwner
            it.viewModel = signInViewModel
            it.fragment = this
        }

        val backStackEntryCount = parentFragmentManager.backStackEntryCount
        return if (backStackEntryCount == 0) binding.root else {
            Timber.d("Wrapped SwipeDismissFrameLayout(backStackEntryCount=$backStackEntryCount)")
            SwipeDismissFrameLayout(context).apply {
                addView(binding.root)
                addCallback(viewLifecycleOwner, swipeDismissCallback)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeAuthenticateOnPhoneTiming()
        observeAuthenticationResult()
    }

    private fun observeAuthenticateOnPhoneTiming() {
        signInViewModel.authenticateOnPhoneTiming.observeEvent(viewLifecycleOwner) {
            val intent = OpenOnPhoneIntent(
                requireContext(),
                messageResId = R.string.account_sign_in_message_open_on_phone
            )
            startActivity(intent)
        }
    }

    private fun observeAuthenticationResult() {
        signInViewModel.completed.observe(viewLifecycleOwner) {
            // TODO: back to account list.
            it.consume()?.let {
                startActivity(HomeActivity.createIntent())
                requireActivity().finish()
            }
        }

        signInViewModel.error.observe(viewLifecycleOwner) {
            it.consume()?.let(::showSignInError)
        }
    }

    private fun showSignInError(error: TwitterAuthenticationError) {
        when (error) {
            is TwitterAuthenticationError.Network -> showNetworkErrorToast()
            is TwitterAuthenticationError.PlayServices -> {
                playServicesChecker.showErrorResolutionDialog(
                    requireActivity(),
                    errorCode = error.statusCode,
                    requestCode = REQUEST_CODE_RESOLVE_PLAY_SERVICES_ERROR
                ) {
                    signInViewModel.onPlayServicesErrorResolutionResult(canceled = true)
                }
            }
            is TwitterAuthenticationError.UnExpected -> {
                val intent = FailureIntent(
                    requireContext(),
                    messageResId = error.messageResId
                )
                startActivity(intent)
            }
        }//.exhaustive
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_RESOLVE_PLAY_SERVICES_ERROR -> {
                signInViewModel.onPlayServicesErrorResolutionResult()
            }
        }
    }

    @UiThread
    fun showConfirmDialog() {
        findNavController().navigate(toConfirmTwitterSignIn())
    }
}