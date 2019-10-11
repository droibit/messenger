package com.droibit.looking2.core.data.repository

import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.core.data.repository.tweet.TweetRepository
import com.droibit.looking2.core.data.repository.userlist.UserListRepository
import com.droibit.looking2.core.data.source.SourceModule
import dagger.Module

@Module(
    includes = [
        SourceModule::class
    ]
)
object RepositoryModule {

    interface Provider {

        val accountRepository: AccountRepository

        val timelineRepository: TimelineRepository

        val tweetRepository: TweetRepository

        val userListRepository: UserListRepository
    }
}