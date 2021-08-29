package com.droibit.looking2.tweet.ui.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.droibit.looking2.core.di.key.ViewModelKey
import com.droibit.looking2.core.ui.Activities.Tweet.ReplyTweet
import com.droibit.looking2.core.util.lifecycle.DaggerViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import java.util.Optional

@Module(includes = [ViewModelModule.BindingModule::class])
object ViewModelModule {

    @Provides
    fun provideTweetCall(
        workManager: WorkManager,
        replyTweet: Optional<ReplyTweet>
    ): TweetCall {
        return TweetCall(workManager, replyTweet.orElse(null))
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
