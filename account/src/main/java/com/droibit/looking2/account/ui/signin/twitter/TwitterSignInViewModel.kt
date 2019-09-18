package com.droibit.looking2.account.ui.signin.twitter

import androidx.annotation.UiThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.account.R
import com.droibit.looking2.account.ui.signin.twitter.TwitterAuthenticationResult.ErrorType
import com.droibit.looking2.account.ui.signin.twitter.TwitterAuthenticationResult.InProgress
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

    @Inject
    constructor(
        accountRepository: AccountRepository,
        playServicesChecker: PlayServicesChecker
    ) : this(accountRepository, playServicesChecker, MutableLiveData())

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
                        is AuthenticationResult.Success -> {
                            TwitterAuthenticationResult.Success
                        }
                        is AuthenticationResult.Failure -> {
                            TwitterAuthenticationResult.Failure(it.error.toErrorType())
                        }
                    }
                    authenticationResultSink.value = result.toEvent()
                }
        }
    }

    private fun AuthenticationError.toErrorType(): ErrorType {
        return when (this) {
            is AuthenticationError.Network -> ErrorType.Network
            is AuthenticationError.PlayServices -> {
                val status =
                    playServicesChecker.checkStatusCode(this.statusCode) as PlayServicesError
                if (status.isUserResolvableError) {
                    ErrorType.PlayServices(errorStatusCode = this.statusCode)
                } else {
                    ErrorType.UnExpected(R.string.account_sign_in_error_message_unexpected)
                }
            }
            is AuthenticationError.UnExpected -> {
                ErrorType.UnExpected(R.string.account_sign_in_error_message_unexpected)
            }
        }
    }
}