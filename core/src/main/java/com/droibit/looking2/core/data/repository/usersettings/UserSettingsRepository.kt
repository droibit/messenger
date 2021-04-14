package com.droibit.looking2.core.data.repository.usersettings

import com.droibit.looking2.core.data.source.local.usersettings.LocalUserSettingsSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepository @Inject constructor(
    private val localSource: LocalUserSettingsSource
) {
    val numOfTweets: Int
        get() = localSource.numOfTweets
}
