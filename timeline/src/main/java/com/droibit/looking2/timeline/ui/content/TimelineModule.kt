package com.droibit.looking2.timeline.ui.content

import android.view.Menu
import android.view.MenuInflater
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.core.di.key.ViewModelKey
import com.droibit.looking2.core.ui.view.ActionMenu
import com.droibit.looking2.timeline.R
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [TimelineModule.BindingModule::class])
object TimelineModule {

    @Provides
    fun provideViewLifecycleProvider(fragment: TimelineFragment): LifecycleOwner {
        return fragment.viewLifecycleOwner
    }

    @Provides
    fun provideTweetListAdapter(
        fragment: TimelineFragment,
        lifecycleOwner: Lazy<LifecycleOwner>,
        tweetTextProcessor: TweetTextProcessor
    ): TweetListAdapter {
        return TweetListAdapter(
            fragment.requireContext(),
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
        return timelineSource.toGetCall(repository)
    }

    @Provides
    fun provideTweetActionMenu(fragment: TimelineFragment): Menu {
        val context = fragment.requireContext()
        return ActionMenu(context).apply {
            MenuInflater(context).inflate(R.menu.tweet_action, this)
        }
    }

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