package com.droibit.looking2.launch.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.model.account.TwitterAccount
import com.jraska.livedata.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class LaunchViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var accountRepository: AccountRepository

    private lateinit var viewModel: com.droibit.looking2.launch.ui.LaunchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        viewModel = com.droibit.looking2.launch.ui.LaunchViewModel(accountRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun launchDestination_LogInTwitter() {
        val accounts = mock<List<TwitterAccount>> {
            on { this.isEmpty() } doReturn true
        }
        whenever(accountRepository.twitterAccounts())
            .thenReturn(flowOf(accounts))

        viewModel.launchDestination
            .test()
            .assertValue(com.droibit.looking2.launch.ui.LaunchDestination.SIGN_IN_TWITTER)
    }

    @Test
    fun launchDestination_Home() {
        val accounts = mock<List<TwitterAccount>> {
            on { this.isEmpty() } doReturn false
        }
        whenever(accountRepository.twitterAccounts())
            .thenReturn(flowOf(accounts))

        viewModel.launchDestination
            .test()
            .assertValue(com.droibit.looking2.launch.ui.LaunchDestination.HOME)
    }
}
