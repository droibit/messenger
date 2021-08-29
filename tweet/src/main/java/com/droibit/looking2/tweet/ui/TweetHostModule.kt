package com.droibit.looking2.tweet.ui

import android.app.Activity
import android.content.Context
import com.droibit.looking2.core.ui.Activities.Tweet.EXTRA_REPLY_TWEET
import com.droibit.looking2.core.ui.Activities.Tweet.ReplyTweet
import com.droibit.looking2.tweet.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import java.util.Optional
import javax.inject.Named

@InstallIn(ActivityComponent::class)
@Module
object TweetHostModule {

    @Named("display")
    @ActivityScoped
    @Provides
    fun provideReplyTweet(activity: Activity): Optional<ReplyTweet> {
        val replyTweet = activity.intent.extras?.getSerializable(EXTRA_REPLY_TWEET) as? ReplyTweet
        return Optional.ofNullable(replyTweet)
    }

    @Named("hasReplyTweet")
    @Provides
    fun provideHasReplyTweet(@Named("display") replyTweet: Optional<ReplyTweet>): Boolean {
        return replyTweet.isPresent
    }

    @Named("title")
    @Provides
    fun provideTitle(
        @Named("hasReplyTweet") hasReplyTweet: Boolean,
        @ApplicationContext context: Context
    ): String {
        return if (hasReplyTweet)
            context.getString(R.string.tweet_title_reply)
        else
            context.getString(R.string.tweet_title_tweet)
    }

    @Named("tweetTextHint")
    @Provides
    fun provideTweetTextHint(
        @Named("hasReplyTweet") hasReplyTweet: Boolean,
        @ApplicationContext context: Context
    ): String {
        return if (hasReplyTweet)
            context.getString(R.string.tweet_text_reply_hint)
        else
            context.getString(R.string.tweet_text_tweet_hint)
    }

    @Named("replyUser")
    @Provides
    fun provideReplyUser(@Named("display") replyTweet: Optional<ReplyTweet>): String {
        return if (replyTweet.isPresent) "@${replyTweet.get().user.screenName}" else ""
    }
}
