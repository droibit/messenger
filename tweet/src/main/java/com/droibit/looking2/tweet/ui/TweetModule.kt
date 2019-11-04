package com.droibit.looking2.tweet.ui

import android.os.Bundle
import com.droibit.looking2.core.di.scope.FeatureScope
import com.droibit.looking2.tweet.R
import com.droibit.looking2.tweet.ui.chooser.TweetChooserFragment
import com.droibit.looking2.tweet.ui.input.keyboard.KeyboardTweetFragment
import com.droibit.looking2.tweet.ui.input.keyboard.KeyboardTweetModule
import com.droibit.looking2.ui.Activities.Tweet.EXTRA_REPLY_TWEET
import com.droibit.looking2.ui.Activities.Tweet.ReplyTweet
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import javax.inject.Named

@Module(
    includes = [
        AndroidInjectionModule::class,
        TweetModule.FragmentBindingModule::class
    ]
)
object TweetModule {

    @FeatureScope
    @Named("hasReplyTweet")
    @Provides
    fun provideHasReplyTweet(activity: TweetActivity): Boolean {
        return requireNotNull(activity.intent.extras).getSerializable(EXTRA_REPLY_TWEET) != null
    }

    @FeatureScope
    @Named("title")
    @Provides
    fun provideTitle(@Named("hasReplyTweet") hasReplyTweet: Boolean, activity: TweetActivity): String {
        return if (hasReplyTweet)
            activity.getString(R.string.tweet_title_tweet)
        else
            activity.getString(R.string.tweet_title_reply)
    }

    @Named("replyUser")
    @Provides
    fun provideReplyUser(
        @Named("hasReplyTweet") hasReplyTweet: Boolean,
        activity: TweetActivity
    ): String {
        return if (!hasReplyTweet) "" else {
            val replyTweet = requireNotNull(activity.intent.extras).getReplyTweet()
            "@${replyTweet.user.screenName}"
        }
    }

    @Module
    interface FragmentBindingModule {

        @ContributesAndroidInjector
        fun contributeTweetChooserInejector(): TweetChooserFragment

        @ContributesAndroidInjector(modules = [KeyboardTweetModule::class])
        fun contributeKeyboardTweetInejector(): KeyboardTweetFragment
    }
}

private fun Bundle.getReplyTweet(): ReplyTweet {
    return this.getSerializable(EXTRA_REPLY_TWEET) as ReplyTweet
}