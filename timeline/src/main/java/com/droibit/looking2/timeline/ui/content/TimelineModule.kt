package com.droibit.looking2.timeline.ui.content

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.core.ui.view.ActionMenu
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.timeline.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import javax.inject.Provider

@InstallIn(FragmentComponent::class)
@Module
object TimelineFragmentModule {

    @Named("fragment")
    @Provides
    fun provideFragmentLifecycleProvider(fragment: TimelineFragment): LifecycleOwner {
        return fragment.viewLifecycleOwner
    }

    @Provides
    fun provideTweetListAdapter(
        fragment: TimelineFragment,
        contentPadding: ShapeAwareContentPadding,
        @Named("fragment") lifecycleOwner: Provider<LifecycleOwner>,
        tweetTextProcessor: TweetTextProcessor
    ): TweetListAdapter {
        return TweetListAdapter(
            LayoutInflater.from(fragment.requireContext()),
            contentPadding,
            lifecycleOwner,
            tweetTextProcessor,
            fragment::onTweetClick
        )
    }

    @Provides
    fun provideTweetActionMenu(fragment: TimelineFragment): Menu {
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
