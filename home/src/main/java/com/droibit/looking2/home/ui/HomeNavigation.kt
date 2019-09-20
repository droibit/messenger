package com.droibit.looking2.home.ui

import androidx.annotation.IdRes
import com.droibit.looking2.home.R

enum class Navigation(@IdRes private val idRes: Int) {
    TWEET(R.id.action_tweet),
    TIMELINE(R.id.action_timeline),
    MENTIONS(R.id.action_mentions),
    LIST(R.id.action_list),
    ACCOUNTS(R.id.action_accounts),
    SETTINGS(R.id.action_settings);

    companion object {
        fun of(@IdRes idRes: Int) = values().first { it.idRes == idRes }
    }
}