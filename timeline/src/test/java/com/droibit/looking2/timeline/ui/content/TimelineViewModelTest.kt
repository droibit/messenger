package com.droibit.looking2.timeline.ui.content

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
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

private typealias Timeline = List<Tweet>

class TimelineViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var getTimelineCall: TimelineSource.GetCall

    @Spy
    private var isLoadingSink = MutableLiveData(false)

    @Spy
    private var getTimelineResultSink = MutableLiveData<Result<Timeline>>()

    private lateinit var viewModel: TimelineViewModel

    private lateinit var testCoroutineDispatcher: TestCoroutineDispatcher

    @Before
    fun setUp() {
        testCoroutineDispatcher = TestCoroutineDispatcher().also {
            it.pauseDispatcher()
        }
        Dispatchers.setMain(testCoroutineDispatcher)

        viewModel = TimelineViewModel(
            getTimelineCall,
            isLoadingSink,
            getTimelineResultSink
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
    fun timeline() = runBlockingTest {
        val timeline = mock<Timeline>()
        whenever(getTimelineCall.invoke(anyOrNull())).thenReturn(timeline)

        val isLoadingObserver = isLoadingSink.test()
        val timelineObserver = viewModel.timeline.test()
        testCoroutineDispatcher.resumeDispatcher()

        timelineObserver.assertValue(timeline)
        isLoadingObserver.assertValueHistory(false, true, false)
    }

    @Test
    fun isNotEmptyTimeline() = runBlockingTest {
        val timeline = mock<Timeline> {
            on { this.isEmpty() } doReturn false
        }
        whenever(getTimelineCall(anyOrNull())).thenReturn(timeline)

        val timelineObserver = viewModel.isNotEmptyTimeline.test()
        testCoroutineDispatcher.resumeDispatcher()

        timelineObserver.assertValue(true)
    }

    @Test
    fun error() = runBlockingTest {
        val error = mock<TwitterError.UnExpected>()
        whenever(getTimelineCall(anyOrNull()))
            .thenThrow(error)

        val isLoadingObserver = isLoadingSink.test()
        val errorObserver = viewModel.error.test()
        testCoroutineDispatcher.resumeDispatcher()

        errorObserver.assertValue {
            val message = it.peek()
            message is GetTimelineErrorMessage.Toast
        }
        isLoadingObserver.assertValueHistory(false, true, false)
    }
}