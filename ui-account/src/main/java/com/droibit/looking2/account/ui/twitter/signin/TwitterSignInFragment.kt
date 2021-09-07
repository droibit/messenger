package com.droibit.looking2.account.ui.twitter.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navOptions
import androidx.wear.widget.SwipeDismissFrameLayout
import app.cash.exhaustive.Exhaustive
import com.droibit.looking2.account.R
import com.droibit.looking2.account.databinding.FragmentTwitterSigninBinding
import com.droibit.looking2.account.ui.twitter.signin.TwitterSignInFragmentDirections.Companion.toConfirmTwitterSignIn
import com.droibit.looking2.ui.common.Activities.Confirmation.FailureIntent
import com.droibit.looking2.ui.common.Activities.Confirmation.OpenOnPhoneIntent
import com.droibit.looking2.ui.common.ext.addCallback
import com.droibit.looking2.ui.common.ext.navigateSafely
import com.droibit.looking2.ui.common.ext.observeEvent
import com.droibit.looking2.ui.common.ext.showToast
import com.droibit.looking2.ui.common.navigation.DeepLinkDirections.toHome
import com.droibit.looking2.ui.common.widget.PopBackSwipeDismissCallback
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import timber.log.Timber

private const val REQUEST_KEY_SIGN_IN_CONFORMATION = "REQUEST_KEY_SIGN_IN_CONFORMATION"

@AndroidEntryPoint
class TwitterSignInFragment : Fragment() {

    private val navArgs: TwitterSignInFragmentArgs by navArgs()

    @Inject
    lateinit var swipeDismissCallback: PopBackSwipeDismissCallback

    private var _binding: FragmentTwitterSigninBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val signInViewModel: TwitterSignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        observeConformationResult()
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

    private fun observeConformationResult() {
        setFragmentResultListener(REQUEST_KEY_SIGN_IN_CONFORMATION) { _, data ->
            val result = TwitterSignInConfirmationDialogResult(data)
            signInViewModel.onConfirmationDialogResult(result)
            Timber.d("Sign in confirmation result: $result")
        }
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
                if (navArgs.mustSignIn) {
                    findNavController().navigate(
                        toHome(),
                        navOptions { popUpTo(R.id.navGraphAccount) }
                    )
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
        @Exhaustive
        when (error) {
            is TwitterAuthenticationErrorMessage.Toast -> showToast(error)
            is TwitterAuthenticationErrorMessage.FailureConfirmation -> {
                val intent = FailureIntent(
                    requireContext(),
                    messageResId = error.messageResId
                )
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    @UiThread
    fun showConfirmDialog() {
        findNavController().navigateSafely(
            toConfirmTwitterSignIn(
                requestKey = REQUEST_KEY_SIGN_IN_CONFORMATION
            )
        )
    }
}