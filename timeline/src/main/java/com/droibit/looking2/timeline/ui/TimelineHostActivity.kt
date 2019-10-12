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
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

class TimelineHostActivity : FragmentActivity(R.layout.activity_timeline), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    private val destinationSource: DestinationSource by lazy(NONE) {
        val source = requireNotNull(intent).getIntExtra(EXTRA_TIMELINE_SOURCE, -1)
        DestinationSource.valueOf(sourceId = source)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        Timber.d("Start dest: $destinationSource")

        if (savedInstanceState == null) {
            val navController = findNavController(R.id.timelineNavHostFragment)
            val navGraph = navController.navInflater.inflate(R.navigation.timeline_nav_graph)
                .apply {
                    this.startDestination = if (destinationSource == DestinationSource.LISTS)
                        R.id.myListsFragment
                    else
                        R.id.timelineFragment
                }
            navController.setGraph(navGraph, destinationSource.toArgs())
        }
    }

    private enum class DestinationSource(val id: Int) {
        HOME(id = TIMELINE_SOURCE_HOME),
        MENTIONS(id = TIMELINE_SOURCE_MENTIONS),
        LISTS(id = TIMELINE_SOURCE_LISTS);

        fun toArgs(): Bundle? {
            return when (this) {
                HOME -> TimelineFragmentArgs(source = TimelineSource.Home)
                MENTIONS -> TimelineFragmentArgs(source = TimelineSource.Mentions)
                LISTS -> null
            }?.toBundle()
        }

        companion object {
            fun valueOf(sourceId: Int): DestinationSource = values().first { it.id == sourceId }
        }
    }
}
