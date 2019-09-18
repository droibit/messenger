package com.droibit.looking2.account.ui.signin.twitter

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.account.R
import com.droibit.looking2.account.ui.signin.twitter.TwitterAuthenticationResult.Failure
import com.droibit.looking2.account.ui.signin.twitter.TwitterAuthenticationResult.FailureType
import com.droibit.looking2.account.ui.signin.twitter.TwitterAuthenticationResult.InProgress
import com.droibit.looking2.account.ui.signin.twitter.TwitterAuthenticationResult.Success
import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.checker.PlayServicesChecker
import com.droibit.looking2.core.util.toEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.droibit.looking2.core.util.checker.PlayServicesChecker.Status.Error as PlayServicesError

class TwitterSignInViewModel(
    private val accountRepository: AccountRepository,
    private val playServicesChecker: PlayServicesChecker,
    private val authenticationResultSink: MutableLiveData<Event<TwitterAuthenticationResult>>
) : ViewModel() {

    val authenticationResult: LiveData<Event<TwitterAuthenticationResult>>
        get() = authenticationResultSink

    @Inject
    constructor(
        accountRepository: AccountRepository,
        playServicesChecker: PlayServicesChecker
    ) : this(accountRepository, playServicesChecker, MutableLiveData())

    @UiThread
    fun onPlayServicesErrorResolutionResult(canceled: Boolean = false) {
        if (canceled ||
            playServicesChecker.checkStatus() is PlayServicesError
        ) {
            val result =
                Failure(FailureType.UnExpected(R.string.account_sign_in_error_message_play_services))
            authenticationResultSink.value = result.toEvent()
        }
    }

    @UiThread
    fun authenticate() {
        if (authenticationResultSink.value?.peek() is InProgress) {
            return
        }
        authenticationResultSink.value = InProgress(authenticatingOnPhone = false).toEvent()

        viewModelScope.launch {
            accountRepository.authenticateTwitter()
                .collect {
                    val result: TwitterAuthenticationResult = when (it) {
                        is AuthenticationResult.WillAuthenticateOnPhone -> {
                            InProgress(authenticatingOnPhone = true)
                        }
                        is AuthenticationResult.Success -> Success
                        is AuthenticationResult.Failure -> Failure(it.error.toErrorType())
                    }
                    authenticationResultSink.value = result.toEvent()
                }
        }
    }

    private fun AuthenticationError.toErrorType(): FailureType {
        return when (this) {
            is AuthenticationError.Network -> FailureType.Network
            is AuthenticationError.PlayServices -> {
                val status =
                    playServicesChecker.checkStatusCode(this.statusCode) as PlayServicesError
                if (status.isUserResolvableError) {
                    FailureType.PlayServices(errorStatusCode = this.statusCode)
                } else {
                    FailureType.UnExpected(R.string.account_sign_in_error_message_unexpected)
                }
            }
            is AuthenticationError.UnExpected -> {
                FailureType.UnExpected(R.string.account_sign_in_error_message_unexpected)
            }
        }
    }
}