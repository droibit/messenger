package com.droibit.looking2.home.ui

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.data.repository.account.AccountRepository
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(
    private val accountRepository: AccountRepository,
    private val activeAccountNameSink: MutableLiveData<String>
) : ViewModel() {

    @get:UiThread
    val activeAccountName: LiveData<String> by lazy(NONE) {
        viewModelScope.launch {
            accountRepository.twitterAccounts()
                .map { accounts -> accounts.firstOrNull { it.active }?.name ?: "" }
                .distinctUntilChanged()
                .collect {
                    activeAccountNameSink.value = it
                }
        }
        activeAccountNameSink
    }

    @Inject
    constructor(accountRepository: AccountRepository) : this(
        accountRepository,
        MutableLiveData()
    )
}
