package com.droibit.looking2.timeline.ui.content

import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.core.model.tweet.Tweet
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GetMyListsTimelineCallTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var timelineRepository: TimelineRepository

    private var listId = Long.MAX_VALUE

    private lateinit var call: GetMyListsTimelineCall

    @Before
    fun setUp() {
        call = GetMyListsTimelineCall(listId, timelineRepository)
    }

    @Test
    fun invoke_getUserListTimeline() = runBlockingTest {
        val timeline = mock<List<Tweet>>()
        whenever(timelineRepository.getUserListTimeline(anyLong(), anyOrNull()))
            .thenReturn(timeline)

        val actualTimeline = call(sinceId = null)
        assertThat(actualTimeline).isEqualTo(timeline)
        verify(timelineRepository).getUserListTimeline(listId, sinceId = null)
    }
}
