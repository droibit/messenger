package com.droibit.looking2.tweet.ui.input

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.droibit.looking2.ui.common.Event
import com.jraska.livedata.test
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.verify

class TweetViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Spy
    private var tweetText = MutableLiveData<String>()

    @Mock
    private lateinit var tweetCall: TweetCall

    @Spy
    private var tweetCompletedSink = MutableLiveData<Event<Unit>>()

    private lateinit var viewModel: TweetViewModel

    @Before
    fun setUp() {
        viewModel = TweetViewModel(
            tweetText,
            tweetCall,
            tweetCompletedSink
        )
    }

    @Test
    fun tweetCompleted() {
        val testObserver = viewModel.tweetCompleted.test()

        val event1 = Event(Unit)
        tweetCompletedSink.value = event1
        val event2 = Event(Unit)
        tweetCompletedSink.value = event2

        testObserver.assertValueHistory(event1, event2)
    }

    @Test
    fun tweet_enqueue() {
        val text = "tweet_text"
        tweetText.value = text

        val testObserver = tweetCompletedSink.test()
        viewModel.tweet()

        verify(tweetCall).enqueue(text)
        testObserver.assertValue(Event(Unit))
    }
}
