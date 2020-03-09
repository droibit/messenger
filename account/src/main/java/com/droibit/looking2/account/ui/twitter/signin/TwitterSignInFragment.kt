package com.droibit.looking2.account.ui.twitter.signin

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
import com.droibit.looking2.account.ui.twitter.signin.TwitterSignInFragmentDirections.Companion.toConfirmTwitterSignIn
import com.droibit.looking2.core.ui.widget.PopBackSwipeDismissCallback
import com.droibit.looking2.core.util.checker.PlayServicesChecker
import com.droibit.looking2.core.util.ext.addCallback
import com.droibit.looking2.core.util.ext.navigateSafely
import com.droibit.looking2.core.util.ext.observeEvent
import com.droibit.looking2.core.util.ext.showToast
import com.droibit.looking2.ui.Activities.Confirmation.FailureIntent
import com.droibit.looking2.ui.Activities.Confirmation.OpenOnPhoneIntent
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import com.droibit.looking2.ui.Activities.Home as HomeActivity

private const val REQUEST_CODE_RESOLVE_PLAY_SERVICES_ERROR = 1

class TwitterSignInFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var playServicesChecker: PlayServicesChecker

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    @field:[Inject Named("needTwitterSignIn")]
    @JvmField
    var needTwitterSignIn: Boolean = false

    private var _binding: FragmentTwitterSigninBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val signInViewModel: TwitterSignInViewModel by navGraphViewModels(R.id.navigationTwitterSignIn) {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTwitterSigninBinding.inflate(inflater, container, false).also {
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
            it.consume()?.let {
                if (needTwitterSignIn) {
                    startActivity(HomeActivity.createIntent())
                    requireActivity().finish()
                } else {
                    findNavController().popBackStack()
                }
            }
        }

        signInViewModel.error.observe(viewLifecycleOwner) {
            it.consume()?.let(::showSignInError)
        }
    }

    private fun showSignInError(error: TwitterAuthenticationErrorMessage) {
        when (error) {
            is TwitterAuthenticationErrorMessage.Toast -> showToast(error)
            is TwitterAuthenticationErrorMessage.PlayServicesDialog -> {
                playServicesChecker.showErrorResolutionDialog(
                    requireActivity(),
                    errorCode = error.statusCode,
                    requestCode = REQUEST_CODE_RESOLVE_PLAY_SERVICES_ERROR
                ) {
                    signInViewModel.onPlayServicesErrorResolutionResult(canceled = true)
                }
            }
            is TwitterAuthenticationErrorMessage.FailureConfirmation -> {
                val intent = FailureIntent(
                    requireContext(),
                    messageResId = error.messageResId
                )
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_RESOLVE_PLAY_SERVICES_ERROR -> {
                signInViewModel.onPlayServicesErrorResolutionResult()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    @UiThread
    fun showConfirmDialog() {
        findNavController().navigateSafely(toConfirmTwitterSignIn())
    }
}