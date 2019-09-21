package com.droibit.looking2.home.ui

import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.data.repository.account.AccountRepository
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel(
    private val accountRepository: AccountRepository,
    private val activeAccountNameSink: MutableLiveData<String>
) : ViewModel(), LifecycleObserver {

    val activeAccountName: LiveData<String>
        get() = activeAccountNameSink

    @Inject
    constructor(accountRepository: AccountRepository) : this(accountRepository, MutableLiveData())

    @OnLifecycleEvent(ON_CREATE)
    fun onCreate() {
        viewModelScope.launch {
            @Suppress("EXPERIMENTAL_API_USAGE")
            accountRepository.twitterAccounts
                .asFlow()
                .map { requireNotNull(accountRepository.activeTwitterAccount()) }
                .collect {
                    activeAccountNameSink.value = "@${it.name}"
                }
        }
    }
}