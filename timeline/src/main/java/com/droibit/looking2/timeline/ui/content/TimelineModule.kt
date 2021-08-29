package com.droibit.looking2.timeline.ui.content

import android.content.Context
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.core.di.key.ViewModelKey
import com.droibit.looking2.core.ui.view.ActionMenu
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.timeline.R
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.multibindings.IntoMap
import javax.inject.Named
import javax.inject.Provider

@InstallIn(FragmentComponent::class)
@Module(includes = [TimelineModule.BindingModule::class])
object TimelineModule {

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
    fun provideTimelineSource(fragment: TimelineFragment): TimelineSource {
        return fragment.args.source
    }

    @Provides
    fun provideGetTimelineCall(
        repository: TimelineRepository,
        timelineSource: TimelineSource
    ): TimelineSource.GetCall {
        return TimelineSource.GetCall(timelineSource, repository)
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

    @Deprecated("Migrate to dagger hilt.")
    @Module
    interface BindingModule {

        @Binds
        @IntoMap
        @ViewModelKey(TimelineViewModel::class)
        fun bindTimelineViewModel(viewModel: TimelineViewModel): ViewModel

        @Binds
        @IntoMap
        @ViewModelKey(TweetActionViewModel::class)
        fun bindTweetActionViewModel(viewModel: TweetActionViewModel): ViewModel
    }
}
