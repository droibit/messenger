package com.droibit.looking2.tweet.ui

import com.droibit.looking2.core.ui.Activities.Tweet.EXTRA_REPLY_TWEET
import com.droibit.looking2.core.ui.Activities.Tweet.ReplyTweet
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.tweet.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped
import java.util.Optional
import javax.inject.Named

@InstallIn(ActivityRetainedComponent::class)
@Module
object TweetHostModule {

    @ActivityRetainedScoped
    @Provides
    fun provideReplyTweet(activity: TweetHostActivity): Optional<ReplyTweet> {
        val replyTweet = activity.intent.extras?.getSerializable(EXTRA_REPLY_TWEET) as? ReplyTweet
        return Optional.ofNullable(replyTweet)
    }

    @ActivityRetainedScoped
    @Named("hasReplyTweet")
    @Provides
    fun provideHasReplyTweet(replyTweet: Optional<ReplyTweet>): Boolean {
        return replyTweet.isPresent
    }

    @ActivityRetainedScoped
    @Named("title")
    @Provides
    fun provideTitle(
        @Named("hasReplyTweet") hasReplyTweet: Boolean,
        activity: TweetHostActivity
    ): String {
        return if (hasReplyTweet)
            activity.getString(R.string.tweet_title_reply)
        else
            activity.getString(R.string.tweet_title_tweet)
    }

    @Named("tweetTextHint")
    @Provides
    fun provideTweetTextHint(
        @Named("hasReplyTweet") hasReplyTweet: Boolean,
        activity: TweetHostActivity
    ): String {
        return if (hasReplyTweet)
            activity.getString(R.string.tweet_text_reply_hint)
        else
            activity.getString(R.string.tweet_text_tweet_hint)
    }

    @ActivityRetainedScoped
    @Named("replyUser")
    @Provides
    fun provideReplyUser(replyTweet: Optional<ReplyTweet>): String {
        return if (replyTweet.isPresent) "@${replyTweet.get().user.screenName}" else ""
    }
}

@InstallIn(ActivityComponent::class)
@Module
object TweetHostActivityModule {
    @ActivityScoped
    @Provides
    fun provideContentPadding(activity: TweetHostActivity): ShapeAwareContentPadding {
        return ShapeAwareContentPadding(activity)
    }
}
