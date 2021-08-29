package com.droibit.looking2.core.data.repository

import com.droibit.looking2.core.data.repository.account.AccountRepository
import com.droibit.looking2.core.data.repository.timeline.TimelineRepository
import com.droibit.looking2.core.data.repository.tweet.TweetRepository
import com.droibit.looking2.core.data.repository.userlist.UserListRepository
import com.droibit.looking2.core.data.repository.usersettings.UserSettingsRepository
import com.droibit.looking2.core.data.source.SourceModule
import dagger.Module
import dagger.hilt.migration.DisableInstallInCheck

// TODO: Should delete.
@DisableInstallInCheck
@Module(
    includes = [
        SourceModule::class
    ]
)
object RepositoryModule {

    @Deprecated("Migrate to dagger hilt.")
    interface Provider {

        val accountRepository: AccountRepository

        val timelineRepository: TimelineRepository

        val tweetRepository: TweetRepository

        val userListRepository: UserListRepository

        val userSettingsRepository: UserSettingsRepository
    }
}
