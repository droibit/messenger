package com.droibit.looking2.timeline.ui.content

import android.content.Context
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.timeline.R
import com.droibit.looking2.ui.common.view.ActionMenu
import com.droibit.looking2.ui.common.view.ShapeAwareContentPadding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(FragmentComponent::class)
@Module
object TimelineFragmentModule {

    @Provides
    fun provideOnTweetItemClickListener(fragment: Fragment) =
        fragment as TweetListAdapter.OnItemClickListener

    @Provides
    fun provideTweetListAdapter(
        fragment: Fragment,
        contentPadding: ShapeAwareContentPadding,
        tweetTextProcessor: TweetTextProcessor,
        onItemClickListener: TweetListAdapter.OnItemClickListener
    ): TweetListAdapter {
        return TweetListAdapter(
            contentPadding,
            { fragment.viewLifecycleOwner },
            tweetTextProcessor,
            onItemClickListener
        )
    }

    @Provides
    fun provideTweetActionMenu(fragment: Fragment): Menu {
        val context = fragment.requireContext()
        return ActionMenu(context).apply {
            MenuInflater(context).inflate(R.menu.tweet_action, this)
        }
    }

    @Provides
    fun provideRemoteActivityHelper(
        @ApplicationContext context: Context
    ) = RemoteActivityHelper(context)
}

@InstallIn(ViewModelComponent::class)
@Module
object TimelineViewModelModule {
    @Provides
    fun provideTimelineSource(handle: SavedStateHandle): TimelineSource {
        val args = TimelineFragmentArgs.fromSavedStateHandle(handle)
        return args.source
    }

    @Provides
    fun provideGetTimelineCall(
        repository: TimelineRepository,
        timelineSource: TimelineSource
    ): TimelineSource.GetCall {
        return TimelineSource.GetCall(timelineSource, repository)
    }
}
