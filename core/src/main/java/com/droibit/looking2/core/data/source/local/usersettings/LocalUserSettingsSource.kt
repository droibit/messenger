package com.droibit.looking2.core.data.source.local.usersettings

import android.content.SharedPreferences
import androidx.core.content.edit
import com.droibit.looking2.core.data.source.local.PreferenceKey
import com.droibit.looking2.core.data.source.local.getInt
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LocalUserSettingsSource @Inject constructor(
    @Named("default") private val sharedPrefs: SharedPreferences,
    private val keys: Keys
) {
    var numOfTweets: Int
        get() = sharedPrefs.getInt(keys.numOfTweets)
        set(value) {
            sharedPrefs.edit { putInt(keys.numOfTweets.key, value) }
        }

    interface Keys {
        val numOfTweets: PreferenceKey<Int>
    }
}
