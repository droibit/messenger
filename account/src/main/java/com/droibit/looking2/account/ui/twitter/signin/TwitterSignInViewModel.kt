package com.droibit.looking2.account.ui.twitter.signin

import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cash.exhaustive.Exhaustive
import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.ui.dialog.DialogButtonResult
import com.droibit.looking2.core.ui.dialog.isPositive
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.ext.toErrorEventLiveData
import com.droibit.looking2.core.util.ext.toSuccessEventLiveData
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TwitterSignInViewModel(
    private val accountRepository: AccountRepository,
    private val isProcessingSink: MutableLiveData<Boolean>,
    private val authenticationResultSink: MutableLiveData<Result<Unit>>,
    private val authenticateOnPhoneTimingSink: MutableLiveData<Event<Unit>>
) : ViewModel() {

    private var signInJob: Job? = null

    val authenticateOnPhoneTiming: LiveData<Event<Unit>>
        get() = authenticateOnPhoneTimingSink

    val isProcessing: LiveData<Boolean>
        get() = isProcessingSink

    val completed: LiveData<Event<Unit>> = authenticationResultSink.toSuccessEventLiveData()

    val error: LiveData<Event<TwitterAuthenticationErrorMessage>> =
        authenticationResultSink.toErrorEventLiveData()

    @Inject
    constructor(
        accountRepository: AccountRepository
    ) : this(
        accountRepository = accountRepository,
        isProcessingSink = MutableLiveData(false),
        authenticationResultSink = MutableLiveData(),
        authenticateOnPhoneTimingSink = MutableLiveData()
    )

    @UiThread
    fun onConfirmationDialogResult(dialogResult: DialogButtonResult) {
        if (dialogResult.isPositive) {
            authenticate()
        }
    }

    @VisibleForTesting
    fun authenticate() {
        if (signInJob?.isActive == true) {
            return
        }

        signInJob = viewModelScope.launch {
            isProcessingSink.value = true
            accountRepository.signInTwitter()
                .collect {
                    @Exhaustive
                    when (it) {
                        is AuthenticationResult.WillAuthenticateOnPhone -> {
                            authenticateOnPhoneTimingSink.value = Event(Unit)
                        }
                        is AuthenticationResult.Success -> {
                            authenticationResultSink.value = Result.success(Unit)
                        }
                        is AuthenticationResult.Failure -> {
                            authenticationResultSink.value = Result.failure(
                                TwitterAuthenticationErrorMessage(
                                    source = it.error,
                                )
                            )
                        }
                    }
                }
            isProcessingSink.value = false
        }
    }
}
