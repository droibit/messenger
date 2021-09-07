package com.droibit.looking2.home.ui

import androidx.annotation.IdRes
import com.droibit.looking2.home.R

internal enum class HomeNavigation(@IdRes private val idRes: Int) {
    TWEET(R.id.action_tweet),
    TIMELINE(R.id.action_timeline),
    MENTIONS(R.id.action_mentions),
    LISTS(R.id.action_lists),
    ACCOUNTS(R.id.action_accounts),
    SETTINGS(R.id.action_settings);

    companion object {
        operator fun invoke(@IdRes idRes: Int) = values().first { it.idRes == idRes }
    }
}