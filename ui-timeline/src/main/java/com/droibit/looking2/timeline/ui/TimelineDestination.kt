package com.droibit.looking2.timeline.ui

import androidx.navigation.NavDirections
import com.droibit.looking2.timeline.ui.TimelineTrampolineFragmentDirections.Companion.toMyLists
import com.droibit.looking2.timeline.ui.TimelineTrampolineFragmentDirections.Companion.toTimeline
import com.droibit.looking2.timeline.ui.content.TimelineSource

internal enum class TimelineDestination(val id: String, val label: String) {
    HOME(id = "home", label = "Home"),
    MENTIONS(id = "mentions", label = "Mentions"),
    LISTS(id = "mylists", label = "Lists");

    val overrideLabel: String get() = "Timeline: $label"

    fun toDirections(): NavDirections {
        return when (this) {
            HOME -> toTimeline(TimelineSource.Home, overrideLabel)
            MENTIONS -> toTimeline(TimelineSource.Mentions, overrideLabel)
            LISTS -> toMyLists()
        }
    }

    companion object {
        operator fun invoke(id: String): TimelineDestination {
            return values().firstOrNull { it.id == id } ?: error("Unknown id($id).")
        }
    }
}
