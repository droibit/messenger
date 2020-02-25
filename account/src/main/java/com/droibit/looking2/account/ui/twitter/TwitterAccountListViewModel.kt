package com.droibit.looking2.account.ui.twitter

import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.account.ui.twitter.TwitterAccountAction.SIGN_OUT
import com.droibit.looking2.account.ui.twitter.TwitterAccountAction.SWITCH_ACCOUNT
import com.droibit.looking2.account.ui.twitter.signout.SignOutConfirmationDialogResult
import com.droibit.looking2.core.config.AccountConfiguration
import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.ui.dialog.isPositive
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.ext.requireValue
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class TwitterAccountListViewModel(
    private val accountRepository: AccountRepository,
    private val accountConfig: AccountConfiguration,
    private val accountsSink: MutableLiveData<List<TwitterAccount>>,
    private val selectedAccountSink: MutableLiveData<Event<TwitterAccount>>,
    private val signInTwitterSink: MutableLiveData<Event<Unit>>,
    private val limitSignInTwitterErrorMessageSink: MutableLiveData<Event<LimitSignInErrorMessage>>,
    private val showSignOutConfirmationSink: MutableLiveData<Event<TwitterAccount>>,
    private val restartAppSink: MutableLiveData<Event<Unit>>
) : ViewModel() {

    @get:UiThread
    val twitterAccounts: LiveData<List<TwitterAccount>> by lazy(NONE) {
        viewModelScope.launch {
            accountRepository.twitterAccounts().collect {
                if (it.isNotEmpty()) {
                    accountsSink.value = it
                } else {
                    restartAppSink.value = Event(Unit)
                }
            }
        }
        accountsSink
    }

    val showSignOutConfirmation: LiveData<Event<TwitterAccount>>
        get() = showSignOutConfirmationSink

    val restartApp: LiveData<Event<Unit>>
        get() = restartAppSink

    val signTwitter: LiveData<Event<Unit>>
        get() = signInTwitterSink

    val limitSignInTwitterErrorMessage: LiveData<Event<LimitSignInErrorMessage>>
        get() = limitSignInTwitterErrorMessageSink

    @Inject
    constructor(
        accountRepository: AccountRepository,
        accountConfig: AccountConfiguration
    ) : this(
        accountRepository,
        accountConfig,
        MutableLiveData(),
        MutableLiveData(),
        MutableLiveData(),
        MutableLiveData(),
        MutableLiveData(),
        MutableLiveData()
    )

    @UiThread
    fun onAddAccountButtonClick() {
        val accounts = accountsSink.value ?: return
        if (accounts.size >= accountConfig.maxNumOfTwitterAccounts) {
            limitSignInTwitterErrorMessageSink.value =
                Event(LimitSignInErrorMessage(accountConfig.maxNumOfTwitterAccounts))
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
    fun onAccountActionItemClick(accountAction: TwitterAccountAction) {
        Timber.d("#onAccountActionItemClick($accountAction)")
        selectedAccountSink.requireValue().consume()?.let { account ->
            when (accountAction) {
                SWITCH_ACCOUNT -> {
                    switchActiveAccount(account)
                }
                SIGN_OUT -> {
                    showSignOutConfirmationSink.value = Event(account)
                }
            }
        }
    }

    private fun switchActiveAccount(account: TwitterAccount) {
        viewModelScope.launch {
            accountRepository.updateActiveTwitterAccount(account.id)
        }
    }

    @UiThread
    fun onSignOutConfirmationDialogResult(dialogResult: SignOutConfirmationDialogResult) {
        if (dialogResult.isPositive) {
            signOutAccount(dialogResult.account)
        }
    }

    @VisibleForTesting
    fun signOutAccount(account: TwitterAccount) {
        viewModelScope.launch {
            accountRepository.signOutTwitter(account.id)
        }
    }
}