package com.droibit.looking2.account.ui.twitter

import android.content.DialogInterface.BUTTON_NEGATIVE
import android.content.DialogInterface.BUTTON_POSITIVE
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.droibit.looking2.account.ui.twitter.TwitterAccountAction.SIGN_OUT
import com.droibit.looking2.account.ui.twitter.TwitterAccountAction.SWITCH_ACCOUNT
import com.droibit.looking2.account.ui.twitter.signout.SignOutConfirmationDialogResult
import com.droibit.looking2.core.config.AccountConfiguration
import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.util.Event
import com.jraska.livedata.test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
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

class TwitterAccountListViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var accountRepository: AccountRepository

    @Mock
    private lateinit var accountConfig: AccountConfiguration

    @Spy
    private var accountsSink = MutableLiveData<List<TwitterAccount>>()

    @Spy
    private var selectedAccountSink = MutableLiveData<Event<TwitterAccount>>()

    @Spy
    private var signInTwitterSink = MutableLiveData<Event<Unit>>()

    @Spy
    private var limitSignInTwitterErrorMessageSink =
        MutableLiveData<Event<LimitSignInErrorMessage>>()

    @Spy
    private var showSignOutConfirmationSink = MutableLiveData<Event<TwitterAccount>>()

    @Spy
    private var restartAppTimingSink = MutableLiveData<Event<Unit>>()

    private lateinit var testCoroutineDispatcher: TestCoroutineDispatcher

    private lateinit var viewModel: TwitterAccountListViewModel

    @Before
    fun setUp() {
        testCoroutineDispatcher = TestCoroutineDispatcher()
        Dispatchers.setMain(testCoroutineDispatcher)
        viewModel = TwitterAccountListViewModel(
            accountRepository,
            accountConfig,
            accountsSink,
            selectedAccountSink,
            signInTwitterSink,
            limitSignInTwitterErrorMessageSink,
            showSignOutConfirmationSink,
            restartAppTimingSink
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun twitterAccounts_showAccounts() = runBlockingTest {
        val accounts = mock<List<TwitterAccount>> {
            on { this.isEmpty() } doReturn false
        }
        val accountsFlow = flowOf(accounts)
        whenever(accountRepository.twitterAccounts())
            .thenReturn(accountsFlow)

        testCoroutineDispatcher.pauseDispatcher()
        val accountsObserver = viewModel.twitterAccounts.test()
        val restartAppTimingObserver = restartAppTimingSink.test()
        testCoroutineDispatcher.resumeDispatcher()

        accountsObserver.assertValue(accounts)
        restartAppTimingObserver.assertNoValue()

        verify(accountRepository).twitterAccounts()
    }

    @Test
    fun twitterAccounts_restartApp() {
        val accounts = mock<List<TwitterAccount>> {
            on { this.isEmpty() } doReturn true
        }
        val accountsFlow = flowOf(accounts)
        whenever(accountRepository.twitterAccounts())
            .thenReturn(accountsFlow)

        testCoroutineDispatcher.pauseDispatcher()
        val accountsObserver = viewModel.twitterAccounts.test()
        val restartAppTimingObserver = restartAppTimingSink.test()
        testCoroutineDispatcher.resumeDispatcher()

        restartAppTimingObserver.assertValue(Event(Unit))
        accountsObserver.assertNoValue()

        verify(accountRepository).twitterAccounts()
    }

    @Test
    fun showSignOutConfirmation() {
        val testObserver = viewModel.showSignOutConfirmation.test()

        val event1 = mock<Event<TwitterAccount>>()
        showSignOutConfirmationSink.value = event1
        val event2 = mock<Event<TwitterAccount>>()
        showSignOutConfirmationSink.value = event2

        testObserver.assertValueHistory(event1, event2)
    }

    @Test
    fun restartApp() {
        val testObserver = viewModel.restartApp.test()

        val event1 = mock<Event<Unit>>()
        restartAppTimingSink.value = event1
        val event2 = mock<Event<Unit>>()
        restartAppTimingSink.value = event2

        testObserver.assertValueHistory(event1, event2)
    }

    @Test
    fun signTwitter() {
        val testObserver = viewModel.signTwitter.test()

        val event1 = mock<Event<Unit>>()
        signInTwitterSink.value = event1
        val event2 = mock<Event<Unit>>()
        signInTwitterSink.value = event2

        testObserver.assertValueHistory(event1, event2)
    }

    @Test
    fun limitSignInTwitterErrorMessage() {
        val testObserver = viewModel.limitSignInTwitterErrorMessage.test()

        val event1 = mock<Event<LimitSignInErrorMessage>>()
        limitSignInTwitterErrorMessageSink.value = event1
        val event2 = mock<Event<LimitSignInErrorMessage>>()
        limitSignInTwitterErrorMessageSink.value = event2

        testObserver.assertValueHistory(event1, event2)
    }

    @Test
    fun onAddAccountButtonClick() {
        whenever(accountConfig.maxNumOfTwitterAccounts)
            .thenReturn(3)
        val accounts = mock<List<TwitterAccount>> {
            on { this.size } doReturn 2
        }
        accountsSink.value = accounts

        val signInTwitterObserver = signInTwitterSink.test()
        val errorMessageObserver = limitSignInTwitterErrorMessageSink.test()
        viewModel.onAddAccountButtonClick()

        signInTwitterObserver.assertValue(Event(Unit))
        errorMessageObserver.assertNoValue()
    }

    @Test
    fun onAddAccountButtonClick_limitedSignIn() {
        whenever(accountConfig.maxNumOfTwitterAccounts)
            .thenReturn(3)
        val accounts = mock<List<TwitterAccount>> {
            on { this.size } doReturn 3 doReturn 4
        }
        accountsSink.value = accounts

        val signInTwitterObserver = signInTwitterSink.test()
        val errorMessageObserver = limitSignInTwitterErrorMessageSink.test()
        viewModel.onAddAccountButtonClick()
        viewModel.onAddAccountButtonClick()

        errorMessageObserver.assertValueHistory(
            Event(LimitSignInErrorMessage(maxNumOfAccounts = 3)),
            Event(LimitSignInErrorMessage(maxNumOfAccounts = 3))
        )
        signInTwitterObserver.assertNoValue()
    }

    @Test
    fun onAddAccountButtonClick_skip() {
        val signInTwitterObserver = signInTwitterSink.test()
        val errorMessageObserver = limitSignInTwitterErrorMessageSink.test()
        viewModel.onAddAccountButtonClick()

        errorMessageObserver.assertNoValue()
        signInTwitterObserver.assertNoValue()
    }

    @Test
    fun onAccountActionItemClick_switchAccount() = runBlockingTest {
        val account = mock<TwitterAccount> {
            on { this.id } doReturn 1L
        }
        selectedAccountSink.value = Event(account)

        viewModel.onAccountActionItemClick(SWITCH_ACCOUNT)

        verify(accountRepository).updateActiveTwitterAccount(account.id)
    }

    @Test
    fun onAccountItemClick() {
        val testObserver = selectedAccountSink.test()
        val account = mock<TwitterAccount>()
        viewModel.onAccountItemClick(account)

        testObserver.assertValue(Event(account))
    }

    @Test
    fun onAccountActionItemClick_signOut() = runBlockingTest {
        val account = mock<TwitterAccount>()
        selectedAccountSink.value = Event(account)

        val testObserver = showSignOutConfirmationSink.test()
        viewModel.onAccountActionItemClick(SIGN_OUT)

        testObserver.assertValue(Event(account))
    }

    @Test
    fun onSignOutConfirmationDialogResult_positive() {
        val spyViewModel = spy(viewModel)
        doNothing().whenever(spyViewModel).signOutAccount(any())

        val account = mock<TwitterAccount>()
        val result = mock<SignOutConfirmationDialogResult> {
            on { this.button } doReturn BUTTON_POSITIVE
            on { this.account } doReturn account
        }

        spyViewModel.onSignOutConfirmationDialogResult(result)

        verify(spyViewModel).signOutAccount(account)
    }

    @Test
    fun onSignOutConfirmationDialogResult_negative() {
        val spyViewModel = spy(viewModel)
        doNothing().whenever(spyViewModel).signOutAccount(any())

        val account = mock<TwitterAccount>()
        val result = mock<SignOutConfirmationDialogResult> {
            on { this.button } doReturn BUTTON_NEGATIVE
            on { this.account } doReturn account
        }

        spyViewModel.onSignOutConfirmationDialogResult(result)

        verify(spyViewModel, never()).signOutAccount(any())
    }

    @Test
    fun signOutAccount() = runBlockingTest {
        val account = mock<TwitterAccount>() {
            on { this.id } doReturn 1L
        }
        viewModel.signOutAccount(account)

        verify(accountRepository).signOutTwitter(account.id)
    }
}