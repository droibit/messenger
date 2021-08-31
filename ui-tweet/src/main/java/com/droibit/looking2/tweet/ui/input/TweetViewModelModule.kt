package com.droibit.looking2.tweet.ui.input

import androidx.lifecycle.SavedStateHandle
import androidx.work.WorkManager
import com.droibit.looking2.ui.common.Activities.Tweet.EXTRA_REPLY_TWEET
import com.droibit.looking2.ui.common.Activities.Tweet.ReplyTweet
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import java.util.Optional

@InstallIn(ViewModelComponent::class)
@Module
object TweetViewModelModule {

    @Provides
    fun provideReplyTweet(savedStateHandle: SavedStateHandle): Optional<ReplyTweet> {
        val replyTweet: ReplyTweet? = savedStateHandle.get(EXTRA_REPLY_TWEET)
        return Optional.ofNullable(replyTweet)
    }

    @Provides
    fun provideTweetCall(
        workManager: WorkManager,
        replyTweet: Optional<ReplyTweet>
    ): TweetCall {
        return TweetCall(workManager, replyTweet.orElse(null))
    }
}
