package com.droibit.looking2.tweet.ui

import com.droibit.looking2.core.di.scope.FeatureScope
import com.droibit.looking2.core.util.Optional
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
    @Provides
    fun provideReplyTweet(activity: TweetActivity): Optional<ReplyTweet> {
        val replyTweet = activity.intent.extras?.getSerializable(EXTRA_REPLY_TWEET) as? ReplyTweet
        return Optional(replyTweet)
    }

    @FeatureScope
    @Named("hasReplyTweet")
    @Provides
    fun provideHasReplyTweet(replyTweet: Optional<ReplyTweet>): Boolean {
        return replyTweet.isPresent
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

    @FeatureScope
    @Named("replyUser")
    @Provides
    fun provideReplyUser(replyTweet: Optional<ReplyTweet>): String {
        return if (replyTweet.isPresent) "@${replyTweet.getValue().user.screenName}" else ""
    }

    @Module
    interface FragmentBindingModule {

        @ContributesAndroidInjector
        fun contributeTweetChooserInejector(): TweetChooserFragment

        @ContributesAndroidInjector(modules = [KeyboardTweetModule::class])
        fun contributeKeyboardTweetInejector(): KeyboardTweetFragment
    }
}