package com.droibit.looking2.timeline.ui

import com.droibit.looking2.timeline.ui.TimelineDestination.HOME
import com.droibit.looking2.timeline.ui.TimelineDestination.LISTS
import com.droibit.looking2.timeline.ui.TimelineDestination.MENTIONS
import com.droibit.looking2.timeline.ui.TimelineTrampolineFragmentDirections.Companion.toMyLists
import com.droibit.looking2.timeline.ui.TimelineTrampolineFragmentDirections.Companion.toTimeline
import com.droibit.looking2.timeline.ui.content.TimelineSource
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TimelineDestinationTest {

    @Test
    fun toDirections_toHomeDirections() {
        val home = HOME.toDirections()
        assertThat(home).isEqualTo(toTimeline(TimelineSource.Home))
    }

    @Test
    fun toDirections_toMentionsDirections() {
        val mentions = MENTIONS.toDirections()
        assertThat(mentions).isEqualTo(toTimeline(TimelineSource.Mentions))
    }

    @Test
    fun toDirections_toMyListsDirections() {
        val myLists = LISTS.toDirections()
        assertThat(myLists).isEqualTo(toMyLists())
    }
}
