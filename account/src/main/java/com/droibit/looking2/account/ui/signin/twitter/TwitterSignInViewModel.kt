package com.droibit.looking2.account.ui.signin.twitter

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.account.R
import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.checker.PlayServicesChecker
import com.droibit.looking2.core.util.ext.toErrorEventLiveData
import com.droibit.looking2.core.util.ext.toSuccessEventLiveData
import com.droibit.looking2.core.util.toEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.droibit.looking2.core.util.checker.PlayServicesChecker.Status.Error as PlayServicesError

class TwitterSignInViewModel(
    private val accountRepository: AccountRepository,
    private val playServicesChecker: PlayServicesChecker,
    private val isProcessingSink: MutableLiveData<Boolean>,
    private val authenticationResultSink: MutableLiveData<Result<Unit>>,
    private val authenticateOnPhoneTimingSink: MutableLiveData<Event<Unit>>
) : ViewModel() {

    private var signInJob: Job? = null

    val authenticationResult: LiveData<TwitterAuthenticationResult>
        get() = TODO()

    val authenticateOnPhoneTiming: LiveData<Event<Unit>>
        get() = authenticateOnPhoneTimingSink

    val isProcessing: LiveData<Boolean> = isProcessingSink

    val completed: LiveData<Event<Unit>> = authenticationResultSink.toSuccessEventLiveData()

    val error: LiveData<Event<TwitterAuthenticationError>> =
        authenticationResultSink.toErrorEventLiveData()

    @Inject
    constructor(
        accountRepository: AccountRepository,
        playServicesChecker: PlayServicesChecker
    ) : this(
        accountRepository,
        playServicesChecker,
        MutableLiveData(false),
        MutableLiveData(),
        MutableLiveData()
    )

    @UiThread
    fun onPlayServicesErrorResolutionResult(canceled: Boolean = false) {
        if (canceled ||
            playServicesChecker.checkStatus() is PlayServicesError
        ) {
            val error = TwitterAuthenticationError.UnExpected(
                R.string.account_sign_in_error_message_play_services
            )
            authenticationResultSink.value = Result.failure(error)
        }
    }

    @UiThread
    fun authenticate() {
        if (signInJob?.isActive == true) {
            return
        }

        signInJob = viewModelScope.launch {
            isProcessingSink.value = true
            accountRepository.authenticateTwitter()
                .collect {
                    when (it) {
                        is AuthenticationResult.WillAuthenticateOnPhone -> {
                            authenticateOnPhoneTimingSink.value = Unit.toEvent()
                        }
                        is AuthenticationResult.Success -> {
                            authenticationResultSink.value = Result.success(Unit)
                        }
                        is AuthenticationResult.Failure -> {
                            authenticationResultSink.value = Result.failure(
                                TwitterAuthenticationError(
                                    source = it.error,
                                    playServicesChecker = playServicesChecker
                                )
                            )
                        }
                    }
                }
            isProcessingSink.value = false
        }
    }
}