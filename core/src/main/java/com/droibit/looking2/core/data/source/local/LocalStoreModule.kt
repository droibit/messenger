package com.droibit.looking2.core.data.source.local

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.droibit.looking2.core.R
import com.droibit.looking2.core.data.source.local.usersettings.LocalUserSettingsSource
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object LocalStoreModule {

    @Provides
    fun provideUserSettingsLocalStoreKeys(
        @Named("appContext") context: Context
    ): LocalUserSettingsSource.Keys {
        return object : LocalUserSettingsSource.Keys {
            override val numOfTweets: PreferenceKey<Int> = IntConvertiblePreferenceKey(
                key = context.getString(R.string.pref_twitter_get_tweet_count_key),
                defaultValue = context.resources.getInteger(
                    R.integer.pref_twitter_tweet_count_default
                )
            )
        }
    }

    @Named("default")
    @Provides
    fun provideDefaultSharedPrefs(@Named("appContext") context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
}
