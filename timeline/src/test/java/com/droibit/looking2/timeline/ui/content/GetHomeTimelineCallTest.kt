package com.droibit.looking2.timeline.ui.content

import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.core.model.tweet.Tweet
import com.google.common.truth.Truth.assertThat
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class GetHomeTimelineCallTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var timelineRepository: TimelineRepository

    @InjectMocks
    private lateinit var call: GetHomeTimelineCall

    @Test
    fun invoke_getHomeTimeline() = runBlockingTest {
        val timeline = mock<List<Tweet>>()
        whenever(timelineRepository.getHomeTimeline(anyOrNull()))
            .thenReturn(timeline)

        val actualTimeline = call(sinceId = null)
        assertThat(actualTimeline).isEqualTo(timeline)
        verify(timelineRepository).getHomeTimeline(sinceId = null)
    }
}