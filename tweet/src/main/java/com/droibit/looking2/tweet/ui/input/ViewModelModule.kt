package com.droibit.looking2.tweet.ui.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.droibit.looking2.core.data.repository.tweet.TweetRepository
import com.droibit.looking2.core.di.key.ViewModelKey
import com.droibit.looking2.core.util.Optional
import com.droibit.looking2.core.util.lifecycle.DaggerViewModelFactory
import com.droibit.looking2.ui.Activities.Tweet.ReplyTweet
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module(includes = [ViewModelModule.BindingModule::class])
object ViewModelModule {

    @Provides
    fun provideTweetCall(repository: TweetRepository, replyTweet: Optional<ReplyTweet>): TweetCall {
        return TweetCall(repository, replyTweet.get())
    }

    @Module
    interface BindingModule {

        @Binds
        @IntoMap
        @ViewModelKey(TweetViewModel::class)
        fun bindTweetViewModel(viewModel: TweetViewModel): ViewModel

        @Binds
        fun bindViewModelFactory(factory: DaggerViewModelFactory): ViewModelProvider.Factory
    }
}