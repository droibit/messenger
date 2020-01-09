package com.droibit.looking2.timeline.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import com.droibit.looking2.core.util.analytics.sendScreenView
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

private const val INVALID_SOURCE_ID = Int.MIN_VALUE

class TimelineHostActivity : FragmentActivity(R.layout.activity_timeline_host),
    HasAndroidInjector,
    NavController.OnDestinationChangedListener {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var analytics: AnalyticsHelper

    private val destinationSource: DestinationSource by lazy(NONE) {
        val intent = requireNotNull(intent)
        val sourceId = intent.getIntExtra(EXTRA_TIMELINE_SOURCE, INVALID_SOURCE_ID).also {
            check(it != INVALID_SOURCE_ID) { "Timeline source id does not exist." }
        }
        DestinationSource(sourceId)
    }

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        Timber.d("Start dest: $destinationSource")

        // ref https://stackoverflow.com/questions/59275009/fragmentcontainerview-using-findnavcontroller
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.timelineNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_timeline)
            .apply {
                this.startDestination = if (destinationSource == DestinationSource.LISTS)
                    R.id.myListsFragment
                else
                    R.id.timelineFragment
            }
        navController.setGraph(navGraph, destinationSource.toArgs())
        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        if (destination.id == R.id.timelineFragment) {
            val navLabel = requireNotNull(destination.label)
            analytics.sendScreenView("$navLabel: ${destinationSource.label}")
        } else {
            analytics.sendScreenView(destination)
        }
    }

    private enum class DestinationSource(val id: Int, val label: String) {
        HOME(id = TIMELINE_SOURCE_HOME, label = "Home"),
        MENTIONS(id = TIMELINE_SOURCE_MENTIONS, label = "Mentions"),
        LISTS(id = TIMELINE_SOURCE_LISTS, label = "Lists");

        fun toArgs(): Bundle? {
            return when (this) {
                HOME -> TimelineFragmentArgs(source = TimelineSource.Home)
                MENTIONS -> TimelineFragmentArgs(source = TimelineSource.Mentions)
                LISTS -> null
            }?.toBundle()
        }

        companion object {

            operator fun invoke(sourceId: Int) = values().first { it.id == sourceId }
        }
    }
}
