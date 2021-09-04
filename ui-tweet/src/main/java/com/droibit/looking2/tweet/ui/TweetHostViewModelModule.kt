package com.droibit.looking2.tweet.ui

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Optional

@InstallIn(ViewModelComponent::class)
@Module
object TweetHostViewModelModule {

    @Provides
    fun provideReplyTweet(savedStateHandle: SavedStateHandle) =
        Optional.ofNullable(ReplyTweet(savedStateHandle))

    @Provides
    fun provideLayoutString(
        @ApplicationContext context: Context,
        replyTweet: Optional<ReplyTweet>
    ) = TweetLayoutString(context, replyTweet.orElse(null))
}
