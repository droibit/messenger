package com.droibit.looking2.timeline.ui.content

import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.timeline.ui.content.TimelineSource.GetCall
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

@Suppress("ClassName")
class TimelineSource_GetCallTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var timelineRepository: TimelineRepository

    @Test
    fun invoke_toGetHomeTimelineCall() {
        val source = mock<TimelineSource.Home>()
        val call = GetCall(source, timelineRepository)
        assertThat(call).isInstanceOf(GetHomeTimelineCall::class.java)
    }

    @Test
    fun invoke_toGetMentionsTimelineCall() {
        val source = mock<TimelineSource.Mentions>()
        val call = GetCall(source, timelineRepository)
        assertThat(call).isInstanceOf(GetMentionsTimelineCall::class.java)
    }

    @Test
    fun invoke_toGetMyListsTimelineCall() {
        val listId = Long.MAX_VALUE
        val source = mock<TimelineSource.MyLists> {
            on { this.listId } doReturn listId
        }
        val call = GetCall(source, timelineRepository)
        assertThat(call).isInstanceOf(GetMyListsTimelineCall::class.java)

        val getMyListsCall = call as GetMyListsTimelineCall
        assertThat(getMyListsCall.listId).isEqualTo(listId)
    }
}
