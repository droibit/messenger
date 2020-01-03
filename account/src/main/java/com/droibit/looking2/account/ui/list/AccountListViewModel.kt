package com.droibit.looking2.account.ui.list

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.config.AccountConfiguration
import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.ext.requireValue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class AccountListViewModel(
    private val accountRepository: AccountRepository,
    private val accountConfig: AccountConfiguration,
    private val accountsSink: MutableLiveData<List<TwitterAccount>>,
    private val selectedAccountSink: MutableLiveData<Event<TwitterAccount>>,
    private val signInTwitterSink: MutableLiveData<Event<Unit>>,
    private val signInErrorMessageProvider: dagger.Lazy<SignInErrorMessage>,
    private val signInTwitterErrorMessageSink: MutableLiveData<Event<SignInErrorMessage>>,
    private val showSignOutConfirmationSink: MutableLiveData<Event<TwitterAccount>>,
    private val restartAppTimingSink: MutableLiveData<Event<Unit>>
) : ViewModel() {

    val twitterAccounts: LiveData<List<TwitterAccount>> by lazy(NONE) {
        viewModelScope.launch {
            accountRepository.twitterAccounts().collect {
                if (it.isNotEmpty()) {
                    accountsSink.value = it
                } else {
                    restartAppTimingSink.value = Event(Unit)
                }
            }
        }
        accountsSink
    }

    val showSignOutConfirmation: LiveData<Event<TwitterAccount>>
        get() = showSignOutConfirmationSink

    val restartAppTiming: LiveData<Event<Unit>>
        get() = restartAppTimingSink

    val signTwitter: LiveData<Event<Unit>>
        get() = signInTwitterSink

    val signInTwitterErrorMessage: LiveData<Event<SignInErrorMessage>>
        get() = signInTwitterErrorMessageSink

    @Inject
    constructor(
        accountRepository: AccountRepository,
        accountConfig: AccountConfiguration,
        signInErrorMessageProvider: dagger.Lazy<SignInErrorMessage>
    ) : this(
        accountRepository,
        accountConfig,
        MutableLiveData(),
        MutableLiveData(),
        MutableLiveData(),
        signInErrorMessageProvider,
        MutableLiveData(),
        MutableLiveData(),
        MutableLiveData()
    )

    @UiThread
    fun onAddAccountButtonClick() {
        val accounts = accountsSink.value ?: return
        if (accounts.size >= accountConfig.maxNumOfTwitterAccounts) {
            signInTwitterErrorMessageSink.value = Event(signInErrorMessageProvider.get())
        } else {
            signInTwitterSink.value = Event(Unit)
        }
    }

    @UiThread
    fun onAccountItemClick(account: TwitterAccount) {
        Timber.d("#onAccountItemClick($account)")
        selectedAccountSink.value = Event(account)
    }

    @UiThread
    fun onAccountActionItemClick(accountAction: AccountAction) {
        Timber.d("#onAccountActionItemClick($accountAction)")
        selectedAccountSink.requireValue().consume()?.let { account ->
            when (accountAction) {
                AccountAction.SWITCH_ACCOUNT -> {
                    switchActiveAccount(account)
                }
                AccountAction.SIGN_OUT -> {
                    showSignOutConfirmationSink.value = Event(account)
                }
            }
        }
    }

    private fun switchActiveAccount(account: TwitterAccount) {
        viewModelScope.launch {
            accountRepository.updateActiveTwitterAccount(account)
        }
    }

    fun signOutAccount(account: TwitterAccount) {
        viewModelScope.launch {
            accountRepository.signOutTwitter(account)
        }
    }
}