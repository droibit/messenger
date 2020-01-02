package com.droibit.looking2.core.model.account

import com.twitter.sdk.android.core.TwitterSession

data class TwitterAccount(
    val id: Long,
    val name: String,
    val active: Boolean
) {
    companion object
}

fun TwitterSession.toAccount(active: Boolean) = TwitterAccount(
    id = this.userId,
    name = this.userName,
    active = active
)