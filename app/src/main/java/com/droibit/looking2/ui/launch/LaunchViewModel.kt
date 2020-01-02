package com.droibit.looking2.ui.launch

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.ui.launch.LaunchDestination.HOME
import com.droibit.looking2.ui.launch.LaunchDestination.LOGIN_TWITTER
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

internal class LaunchViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    val launchDestination: LiveData<LaunchDestination> = liveData {
        accountRepository.twitterAccounts()
            .collect {
                emit(if (it.isEmpty()) LOGIN_TWITTER else HOME)
            }
    }
}