package com.droibit.looking2.timeline.ui.content

import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.core.model.tweet.Tweet
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class GetMentionsTimelineCallTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var timelineRepository: TimelineRepository

    @InjectMocks
    private lateinit var call: GetMentionsTimelineCall

    @Test
    fun invoke_getMentionsTimeline() = runBlockingTest {
        val timeline = mock<List<Tweet>>()
        whenever(timelineRepository.getMentionsTimeline(anyOrNull()))
            .thenReturn(timeline)

        val actualTimeline = call(sinceId = null)
        assertThat(actualTimeline).isEqualTo(timeline)
        verify(timelineRepository).getMentionsTimeline(sinceId = null)
    }
}