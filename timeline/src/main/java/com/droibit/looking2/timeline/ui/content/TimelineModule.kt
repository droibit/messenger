package com.droibit.looking2.timeline.ui.content

import dagger.Module
import dagger.Provides

@Module
object TimelineModule {

    @Provides
    @JvmStatic
    fun provideTweetListAdapter(
        fragment: TimelineFragment,
        tweetTextProcessor: TweetTextProcessor
    ): TweetListAdapter {
        return TweetListAdapter(
            fragment.requireContext(),
            tweetTextProcessor,
            fragment::onTweetClick
        )
    }
}