package com.droibit.looking2.tweet.ui

import com.droibit.looking2.core.ui.Activities.Tweet.EXTRA_REPLY_TWEET
import com.droibit.looking2.core.ui.Activities.Tweet.ReplyTweet
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.tweet.R
import com.droibit.looking2.tweet.ui.chooser.TweetChooserFragment
import com.droibit.looking2.tweet.ui.input.TweetViewModelModule
import com.droibit.looking2.tweet.ui.input.keyboard.KeyboardTweetFragment
import com.droibit.looking2.tweet.ui.input.voice.VoiceTweetFragment
import com.droibit.looking2.tweet.ui.input.voice.VoiceTweetModule
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import dagger.android.ContributesAndroidInjector
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityScoped
import java.util.Optional
import javax.inject.Named

@InstallIn(ViewModelComponent::class)
@Module(
    includes = [
        AndroidInjectionModule::class,
        TweetHostModule.FragmentBindingModule::class,
        TweetViewModelModule::class
    ]
)
object TweetHostModule {

    @ActivityScoped
    @Provides
    fun provideReplyTweet(activity: TweetHostActivity): Optional<ReplyTweet> {
        val replyTweet = activity.intent.extras?.getSerializable(EXTRA_REPLY_TWEET) as? ReplyTweet
        return Optional.ofNullable(replyTweet)
    }

    @ActivityScoped
    @Named("hasReplyTweet")
    @Provides
    fun provideHasReplyTweet(replyTweet: Optional<ReplyTweet>): Boolean {
        return replyTweet.isPresent
    }

    @ActivityScoped
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

    @ActivityScoped
    @Named("replyUser")
    @Provides
    fun provideReplyUser(replyTweet: Optional<ReplyTweet>): String {
        return if (replyTweet.isPresent) "@${replyTweet.get().user.screenName}" else ""
    }

    @ActivityScoped
    @Provides
    fun provideContentPadding(activity: TweetHostActivity): ShapeAwareContentPadding {
        return ShapeAwareContentPadding(activity)
    }

    @Deprecated("Migrate to dagger hilt.")
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
