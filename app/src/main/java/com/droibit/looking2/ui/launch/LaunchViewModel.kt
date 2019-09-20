package com.droibit.looking2.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.droibit.looking2.core.data.repository.account.AccountRepository
import javax.inject.Inject

internal class LaunchViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    val launchDestination: LiveData<LaunchDestination> = liveData {
        if (accountRepository.activeAccount() == null) {
            emit(LaunchDestination.LOGIN_TWITTER)
        } else {
            emit(LaunchDestination.HOME)
        }
    }
}