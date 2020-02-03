package com.droibit.looking2.account.ui.twitter.signin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.droibit.looking2.account.R
import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.checker.PlayServicesChecker
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class TwitterSignInViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var accountRepository: AccountRepository

    @Mock
    private lateinit var playServicesChecker: PlayServicesChecker

    @Spy
    private var isProcessingSink = MutableLiveData<Boolean>()

    @Spy
    private var authenticationResultSink = MutableLiveData<Result<Unit>>()

    @Spy
    private var authenticateOnPhoneTimingSink = MutableLiveData<Event<Unit>>()

    private lateinit var viewModel: TwitterSignInViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(TestCoroutineDispatcher())

        viewModel = TwitterSignInViewModel(
            accountRepository,
            playServicesChecker,
            isProcessingSink,
            authenticationResultSink,
            authenticateOnPhoneTimingSink
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun authenticateOnPhoneTiming() {
        val testObserver = viewModel.authenticateOnPhoneTiming.test()

        authenticateOnPhoneTimingSink.value = Event(Unit)
        authenticateOnPhoneTimingSink.value = Event(Unit)

        testObserver.assertValueHistory(Event(Unit), Event(Unit))
    }

    @Test
    fun isProcessing() {
        val testObserver = viewModel.isProcessing.test()

        isProcessingSink.value = true
        isProcessingSink.value = false

        testObserver.assertValueHistory(true, false)
    }

    @Test
    fun completed() {
        val testObserver = viewModel.completed.test()

        authenticationResultSink.value = Result.success(Unit)
        authenticationResultSink.value = Result.success(Unit)

        testObserver.assertValueHistory(Event(Unit), Event(Unit))
    }

    @Test
    fun error() {
        val testObserver = viewModel.error.test()

        val error1 = mock<TwitterAuthenticationErrorMessage>()
        authenticationResultSink.value = Result.failure(error1)

        val error2 = mock<TwitterAuthenticationErrorMessage>()
        authenticationResultSink.value = Result.failure(error2)

        testObserver.assertValueHistory(Event(error1), Event(error2))
    }

    @Test
    fun onPlayServicesErrorResolutionResult_available() {
        val testObserver = authenticationResultSink.test()

        val status = mock<PlayServicesChecker.Status>()
        whenever(playServicesChecker.checkStatus()).thenReturn(status)

        viewModel.onPlayServicesErrorResolutionResult(canceled = false)

        testObserver.assertNoValue()
        verify(playServicesChecker).checkStatus()
    }

    @Test
    fun onPlayServicesErrorResolutionResult_unavailable() {
        val testObserver = authenticationResultSink.test()

        val status = mock<PlayServicesChecker.Status.Error>()
        whenever(playServicesChecker.checkStatus()).thenReturn(status)

        viewModel.onPlayServicesErrorResolutionResult(canceled = false)

        val error = TwitterAuthenticationErrorMessage.FailureConfirmation(
            R.string.account_sign_in_error_message_play_services
        )
        testObserver.assertValue(Result.failure(error))
        verify(playServicesChecker).checkStatus()
    }

    @Test
    fun onPlayServicesErrorResolutionResult_canceled() {
        val testObserver = authenticationResultSink.test()

        viewModel.onPlayServicesErrorResolutionResult(canceled = true)

        val error = TwitterAuthenticationErrorMessage.FailureConfirmation(
            R.string.account_sign_in_error_message_play_services
        )
        testObserver.assertValue(Result.failure(error))
        verify(playServicesChecker, never()).checkStatus()
    }

    @Test
    fun authenticate_success() = runBlockingTest {
        val isProcessingObserver = isProcessingSink.test()
        val authenticateOnPhoneTimingObserver = authenticateOnPhoneTimingSink.test()
        val authenticationResultObserver = authenticationResultSink.test()

        val flow = flowOf(
            AuthenticationResult.WillAuthenticateOnPhone,
            AuthenticationResult.Success
        )
        whenever(accountRepository.signInTwitter()).thenReturn(flow)

        viewModel.authenticate()

        isProcessingObserver.assertValueHistory(true, false)
        authenticateOnPhoneTimingObserver.assertValue(Event(Unit))
        authenticationResultObserver.assertValue(Result.success(Unit))

        verify(accountRepository).signInTwitter()
    }

    @Ignore("Not implemented yet.")
    @Test
    fun authenticate_skipExecuting()  {
        fail("Not implemented yet.")
    }

    @Test
    fun authenticate_error() = runBlockingTest {
        val isProcessingObserver = isProcessingSink.test()
        val authenticateOnPhoneTimingObserver = authenticateOnPhoneTimingSink.test()
        val authenticationResultObserver = authenticationResultSink.test()

        val error = mock<AuthenticationError.Network>()
        val flow = flowOf(
            AuthenticationResult.WillAuthenticateOnPhone,
            AuthenticationResult.Failure(error)
        )
        whenever(accountRepository.signInTwitter()).thenReturn(flow)

        viewModel.authenticate()

        isProcessingObserver.assertValueHistory(true, false)
        authenticateOnPhoneTimingObserver.assertValue(Event(Unit))
        authenticationResultObserver.assertValue {
            it.exceptionOrNull() is TwitterAuthenticationErrorMessage.Toast
        }

        verify(accountRepository).signInTwitter()
    }
}