package com.droibit.looking2.launch.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.launch.ui.LaunchDestination.HOME
import com.droibit.looking2.launch.ui.LaunchDestination.SIGN_IN_TWITTER
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect

@HiltViewModel
class LaunchViewModel @Inject constructor(
    private val accountRepository: AccountRepository
) : ViewModel() {

    val launchDestination: LiveData<LaunchDestination> = liveData {
        accountRepository.twitterAccounts()
            .collect {
                emit(if (it.isEmpty()) SIGN_IN_TWITTER else HOME)
            }
    }
}
