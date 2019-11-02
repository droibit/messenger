package com.droibit.looking2.tweet.ui

import com.droibit.looking2.core.di.scope.FeatureScope
import com.droibit.looking2.tweet.R
import com.droibit.looking2.tweet.ui.chooser.TweetChooserFragment
import com.droibit.looking2.ui.Activities.Tweet.EXTRA_REPLY_TWEET
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
    @Named("title")
    @Provides
    fun provideTitle(activity: TweetActivity): String {
        return if (requireNotNull(activity.intent.extras).getSerializable(EXTRA_REPLY_TWEET) == null)
            activity.getString(R.string.tweet_title_tweet)
        else
            activity.getString(R.string.tweet_title_reply)
    }

    @Module
    interface FragmentBindingModule {

        @ContributesAndroidInjector
        fun provideTweetChooserInejector(): TweetChooserFragment
    }
}