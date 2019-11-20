package com.droibit.looking2.home.ui

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.data.repository.account.AccountRepository
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class HomeViewModel(
    private val accountRepository: AccountRepository,
    private val activeAccountNameSink: MutableLiveData<String>
) : ViewModel() {

    @get:UiThread
    val activeAccountName: LiveData<String> by lazy(NONE) {
        // FIXME: Crash when all accounts removed.
        viewModelScope.launch {
            @Suppress("EXPERIMENTAL_API_USAGE")
            accountRepository.twitterAccounts
                .asFlow()
                .map { requireNotNull(accountRepository.activeTwitterAccount()) }
                .collect {
                    activeAccountNameSink.value = "@${it.name}"
                }
        }
        activeAccountNameSink
    }

    @Inject
    constructor(accountRepository: AccountRepository) : this(accountRepository, MutableLiveData())
}