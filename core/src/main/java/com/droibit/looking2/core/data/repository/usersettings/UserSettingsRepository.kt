package com.droibit.looking2.core.data.repository.usersettings

import com.droibit.looking2.core.data.source.local.usersettings.LocalUserSettingsStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepository @Inject constructor(
    private val localStore: LocalUserSettingsStore
) {
    val numOfTweets: Int
        get() = localStore.numOfTweets
}