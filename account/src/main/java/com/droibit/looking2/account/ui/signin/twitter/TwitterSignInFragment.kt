package com.droibit.looking2.account.ui.signin.twitter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.wear.widget.SwipeDismissFrameLayout
import com.droibit.looking2.account.R
import com.droibit.looking2.account.databinding.FragmentTwitterSigninBinding
import com.droibit.looking2.core.ui.dialog.DialogViewModel
import com.droibit.looking2.core.util.ext.observeIfNotHandled
import com.github.droibit.chopstick.resource.bindString
import dagger.android.support.AndroidSupportInjection
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class TwitterSignInFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var binding: FragmentTwitterSigninBinding

    private val dialogViewModel: DialogViewModel by activityViewModels { viewModelFactory }

    private val confirmationMessage: String by bindString(R.string.account_sign_in_message_phone_preparation)

    private val swipeDismissCallback: SwipeDismissFrameLayout.Callback by lazy(NONE) {
        object : SwipeDismissFrameLayout.Callback() {
            override fun onDismissed(layout: SwipeDismissFrameLayout) {
                // Prevent flicker on screen.
                layout.visibility = View.INVISIBLE
                findNavController().popBackStack()
            }
        }
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTwitterSigninBinding.inflate(inflater, container, false).also {
            it.fragment = this
        }

        val backStackEntryCount = requireFragmentManager().backStackEntryCount
        return if (backStackEntryCount == 0) binding.root else {
            Timber.d("Wrapped SwipeDismissFrameLayout(backStackEntryCount=$backStackEntryCount)")
            SwipeDismissFrameLayout(context).apply {
                addView(binding.root)
                addCallback(swipeDismissCallback)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialogViewModel.event.observeIfNotHandled(viewLifecycleOwner) { event ->
            when (event.id.value) {
                R.id.signInConfirmationDialogFragment -> {
                    Timber.d("isok=${event.isOk}")
                }
            }
        }
    }

    override fun onDestroyView() {
        (view as? SwipeDismissFrameLayout)?.removeCallback(swipeDismissCallback)
        super.onDestroyView()
    }

    fun showConfirmDialog() {
        val directions = TwitterSignInFragmentDirections.confirmTwitterSignIn(confirmationMessage)
        findNavController().navigate(directions)
    }
}