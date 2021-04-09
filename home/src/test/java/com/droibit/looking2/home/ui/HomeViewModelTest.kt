package com.droibit.looking2.home.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.model.account.TwitterAccount
import com.jraska.livedata.test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class HomeViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var accountRepository: AccountRepository

    @Spy
    private var activeAccountNameSink = MutableLiveData<String>()

    private lateinit var testCoroutineDispatcher: TestCoroutineDispatcher

    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        testCoroutineDispatcher = TestCoroutineDispatcher()
        Dispatchers.setMain(testCoroutineDispatcher)
        viewModel = HomeViewModel(
            accountRepository, activeAccountNameSink
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun activeAccountName() = runBlockingTest {
        val name1 = "name_1"
        val account1 = mock<TwitterAccount> {
            on { this.name } doReturn name1
            on { this.active } doReturn true doReturn true
        }
        val name2 = "name_2"
        val account2 = mock<TwitterAccount> {
            on { this.name } doReturn name2
            on { this.active } doReturn true doReturn true
        }
        val account3 = mock<TwitterAccount>()
        val accountsFlow = flowOf(
            listOf(account1, account2),
            listOf(account1, account2),
            listOf(account2),
            listOf(account2),
            listOf(account3)
        )
        whenever(accountRepository.twitterAccounts())
            .thenReturn(accountsFlow)

        testCoroutineDispatcher.pauseDispatcher()
        val testObserver = viewModel.activeAccountName.test()
        testCoroutineDispatcher.resumeDispatcher()

        testObserver.assertValueHistory(name1, name2, "")
        verify(accountRepository).twitterAccounts()
    }
}