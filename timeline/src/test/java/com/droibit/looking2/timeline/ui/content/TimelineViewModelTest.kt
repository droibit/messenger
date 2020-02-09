package com.droibit.looking2.timeline.ui.content

import androidx.annotation.CheckResult
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
    private var isLoadingSink = MutableLiveData<Boolean>(false)

    @Spy
    private var getTimelineResultSink = MutableLiveData<Result<Timeline>>()

    @Before
    fun setUp() {
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @CheckResult
    private fun createViewModel(): TimelineViewModel {
        return TimelineViewModel(getTimelineCall, isLoadingSink, getTimelineResultSink)
    }

    @Test
    fun isLoading() {
        val viewModel = createViewModel()
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
        val viewModel = createViewModel()
        val timelineObserver = viewModel.timeline.test()

        timelineObserver.assertValue(timeline)
        isLoadingObserver.assertValueHistory(false, true, false)
    }

    @Test
    fun isNotEmptyTimeline() = runBlockingTest {
        val timeline = mock<Timeline> {
            on { this.isEmpty() } doReturn false
        }
        whenever(getTimelineCall(anyOrNull())).thenReturn(timeline)

        val viewModel = createViewModel()
        val timelineObserver = viewModel.isNotEmptyTimeline.test()

        timelineObserver.assertValue(true)
    }

    @Test
    fun error() = runBlockingTest {
        val error = mock<TwitterError.UnExpected>()
        whenever(getTimelineCall(anyOrNull()))
            .thenThrow(error)

        val isLoadingObserver = isLoadingSink.test()
        val viewModel = createViewModel()
        val errorObserver = viewModel.error.test()

        errorObserver.assertValue {
            val message = it.peek()
            message is GetTimelineErrorMessage.Toast
        }
        isLoadingObserver.assertValueHistory(false, true, false)
    }
}