package com.droibit.looking2.tweet.ui

import com.droibit.looking2.core.di.scope.FeatureScope
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.core.util.Optional
import com.droibit.looking2.tweet.R
import com.droibit.looking2.tweet.ui.chooser.TweetChooserFragment
import com.droibit.looking2.tweet.ui.input.ViewModelModule
import com.droibit.looking2.tweet.ui.input.keyboard.KeyboardTweetFragment
import com.droibit.looking2.tweet.ui.input.voice.VoiceTweetFragment
import com.droibit.looking2.tweet.ui.input.voice.VoiceTweetModule
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
        TweetHostModule.FragmentBindingModule::class,
        ViewModelModule::class
    ]
)
object TweetHostModule {

    @FeatureScope
    @Provides
    fun provideReplyTweet(activity: TweetHostActivity): Optional<ReplyTweet> {
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
    fun provideTitle(@Named("hasReplyTweet") hasReplyTweet: Boolean, activity: TweetHostActivity): String {
        return if (hasReplyTweet)
            activity.getString(R.string.tweet_title_reply)
        else
            activity.getString(R.string.tweet_title_tweet)
    }

    @Named("tweetTextHint")
    @Provides
    fun provideTweetTextHint(@Named("hasReplyTweet") hasReplyTweet: Boolean, activity: TweetHostActivity): String {
        return if (hasReplyTweet)
            activity.getString(R.string.tweet_text_reply_hint)
        else
            activity.getString(R.string.tweet_text_tweet_hint)
    }

    @FeatureScope
    @Named("replyUser")
    @Provides
    fun provideReplyUser(replyTweet: Optional<ReplyTweet>): String {
        return if (replyTweet.isPresent) "@${replyTweet.getValue().user.screenName}" else ""
    }

    @FeatureScope
    @Provides
    fun provideContentPadding(activity: TweetHostActivity): ShapeAwareContentPadding {
        return ShapeAwareContentPadding(activity)
    }

    @Module
    interface FragmentBindingModule {

        @ContributesAndroidInjector
        fun contributeTweetChooserInjector(): TweetChooserFragment

        @ContributesAndroidInjector
        fun contributeKeyboardTweetInjector(): KeyboardTweetFragment

        @ContributesAndroidInjector(modules = [VoiceTweetModule::class])
        fun contributeVoiceTweetInjector(): VoiceTweetFragment
    }
}