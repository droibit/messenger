package com.droibit.looking2.core.model.account

import com.twitter.sdk.android.core.TwitterSession

data class TwitterAccount(val id: Long, val name: String) {
    companion object
}

fun TwitterSession.toAccount() = TwitterAccount(id = this.userId, name = this.userName)