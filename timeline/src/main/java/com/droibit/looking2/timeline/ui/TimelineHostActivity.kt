package com.droibit.looking2.timeline.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.ui.content.TimelineFragmentArgs
import com.droibit.looking2.timeline.ui.content.TimelineSource
import com.droibit.looking2.ui.Activities.Timeline.EXTRA_TIMELINE_SOURCE
import com.droibit.looking2.ui.Activities.Timeline.TIMELINE_SOURCE_HOME
import com.droibit.looking2.ui.Activities.Timeline.TIMELINE_SOURCE_LISTS
import com.droibit.looking2.ui.Activities.Timeline.TIMELINE_SOURCE_MENTIONS
import kotlin.LazyThreadSafetyMode.NONE

class TimelineHostActivity : FragmentActivity(R.layout.activity_timeline) {

    private val startDestination: StartDestination by lazy(NONE) {
        val source = requireNotNull(intent).getIntExtra(EXTRA_TIMELINE_SOURCE, -1)
        StartDestination.valueOf(id = source)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            val navController = findNavController(R.id.timelineNavHostFragment)
            val navGraph = navController.navInflater.inflate(R.navigation.timeline_nav_graph)
                .apply {
                    startDestination = R.id.timelineFragment
                }
            navController.setGraph(navGraph, startDestination.toArgs())
        }
    }

    private enum class StartDestination(val id: Int) {
        HOME(id = TIMELINE_SOURCE_HOME),
        MENTIONS(id = TIMELINE_SOURCE_MENTIONS),
        LISTS(id = TIMELINE_SOURCE_LISTS);

        fun toArgs(): Bundle? {
            return when (this) {
                HOME -> TimelineFragmentArgs(source = TimelineSource.Home).toBundle()
                MENTIONS -> TimelineFragmentArgs(source = TimelineSource.Mentions).toBundle()
                LISTS -> null
            }
        }

        companion object {
            fun valueOf(id: Int): StartDestination = values().first { it.id == id }
        }
    }
}
