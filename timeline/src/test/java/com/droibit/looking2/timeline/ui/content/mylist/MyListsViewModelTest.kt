package com.droibit.looking2.timeline.ui.content.mylist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.droibit.looking2.core.data.repository.userlist.UserListRepository
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.UserList
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
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

private typealias UserLists = List<UserList>

class MyListsViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userListRepository: UserListRepository

    @Spy
    private var isLoadingSink = MutableLiveData(false)

    @Spy
    private var getMyListsResultSink = MutableLiveData<Result<UserLists>>()

    private lateinit var viewModel: MyListsViewModel

    private lateinit var testCoroutineDispatcher: TestCoroutineDispatcher

    @Before
    fun setUp() {
        testCoroutineDispatcher = TestCoroutineDispatcher().also {
            it.pauseDispatcher()
        }
        Dispatchers.setMain(testCoroutineDispatcher)

        viewModel = MyListsViewModel(
            userListRepository,
            isLoadingSink,
            getMyListsResultSink
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun isLoading() {
        val testObserver = viewModel.isLoading.test()

        isLoadingSink.value = true
        isLoadingSink.value = false

        testObserver.assertValueHistory(false, true, false)
    }

    @Test
    fun myLists() = runBlockingTest {
        val userLists = mock<UserLists>()
        whenever(userListRepository.getMyLists()).thenReturn(userLists)

        val isLoadingObserver = isLoadingSink.test()
        val myListsObserver = viewModel.myLists.test()

        testCoroutineDispatcher.resumeDispatcher()

        myListsObserver.assertValue(userLists)
        isLoadingObserver.assertValueHistory(false, true, false)

        verify(userListRepository).getMyLists()
    }

    @Test
    fun isNotEmptyMyLists() = runBlockingTest {
        val userLists = mock<UserLists> {
            on { this.isEmpty() } doReturn false
        }
        whenever(userListRepository.getMyLists()).thenReturn(userLists)

        val testObserver = viewModel.isNotEmptyMyLists.test()

        testCoroutineDispatcher.resumeDispatcher()

        testObserver.assertValue(true)
    }

    @Test
    fun error() = runBlockingTest {
        val error = mock<TwitterError.UnExpected>()
        whenever(userListRepository.getMyLists())
            .thenThrow(error)

        val isLoadingObserver = isLoadingSink.test()
        val errorObserver = viewModel.error.test()

        testCoroutineDispatcher.resumeDispatcher()

        errorObserver.assertValue {
            val message = it.peek()
            message is GetMyListsErrorMessage.Toast
        }
        isLoadingObserver.assertValueHistory(false, true, false)

        verify(userListRepository).getMyLists()
    }
}