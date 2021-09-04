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
    fun overrideLabel() {
        assertThat(HOME.overrideLabel).isEqualTo("Timeline: ${HOME.label}")
        assertThat(MENTIONS.overrideLabel).isEqualTo("Timeline: ${MENTIONS.label}")
        assertThat(LISTS.overrideLabel).isEqualTo("Timeline: ${LISTS.label}")
    }

    @Test
    fun toDirections_toHomeDirections() {
        val home = HOME.toDirections()
        assertThat(home).isEqualTo(toTimeline(TimelineSource.Home, HOME.overrideLabel))
    }

    @Test
    fun toDirections_toMentionsDirections() {
        val mentions = MENTIONS.toDirections()
        assertThat(mentions).isEqualTo(toTimeline(TimelineSource.Mentions, MENTIONS.overrideLabel))
    }

    @Test
    fun toDirections_toMyListsDirections() {
        val myLists = LISTS.toDirections()
        assertThat(myLists).isEqualTo(toMyLists())
    }
}
