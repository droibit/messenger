package com.droibit.looking2.core.model.account

import com.twitter.sdk.android.core.TwitterSession
import java.io.Serializable

data class TwitterAccount(
    val id: Long,
    val name: String,
    val active: Boolean
): Serializable {
    companion object
}

fun TwitterSession.toAccount(active: Boolean) = TwitterAccount(
    id = this.userId,
    name = this.userName,
    active = active
)