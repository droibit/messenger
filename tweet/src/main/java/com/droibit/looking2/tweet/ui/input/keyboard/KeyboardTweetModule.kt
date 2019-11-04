package com.droibit.looking2.tweet.ui.input.keyboard

import com.droibit.looking2.tweet.R
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object KeyboardTweetModule {

    @Named("tweetTextHint")
    @Provides
    fun provideTweetTextHint(@Named("hasReplyTweet") hasReplyTweet: Boolean, fragment: KeyboardTweetFragment): String {
        return if (hasReplyTweet)
            fragment.getString(R.string.tweet_text_reply_hint)
        else
            fragment.getString(R.string.tweet_text_tweet_hint)
    }
}