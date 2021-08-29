package com.droibit.looking2.tweet.ui.input

import androidx.work.WorkManager
import com.droibit.looking2.core.ui.Activities.Tweet.ReplyTweet
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import java.util.Optional

@InstallIn(ViewModelComponent::class)
@Module
object TweetViewModelModule {

    @Provides
    fun provideTweetCall(
        workManager: WorkManager,
        replyTweet: Optional<ReplyTweet>
    ): TweetCall {
        return TweetCall(workManager, replyTweet.orElse(null))
    }
}
