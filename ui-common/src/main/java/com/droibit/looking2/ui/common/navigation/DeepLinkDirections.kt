package com.droibit.looking2.ui.common.navigation

import android.net.Uri
import androidx.core.net.toUri
import com.droibit.looking2.core.model.tweet.Tweet

private const val urlScheme = "looking"

object DeepLinkDirections {
    fun toHome() = "$urlScheme://home".toUri()

    fun toHomeTimeline() = "$urlScheme://timeline/home".toUri()

    fun toMentionsTimeline() = "$urlScheme://timeline/mentions".toUri()

    fun toMyLists() = "$urlScheme://timeline/mylists".toUri()

    fun toSignIn(mustSignIn: Boolean = true) =
        "$urlScheme://account/sign_in?mustSignIn=$mustSignIn".toUri()

    fun toAccounts() = "$urlScheme://account/accounts?mustSignIn=false".toUri()

    fun toSettings() = "$urlScheme://settings".toUri()

    fun toTweet(replyTweet: Tweet? = null): Uri = Uri.parse("$urlScheme://tweet").buildUpon()
        .apply {
            if (replyTweet != null) {
                appendQueryParameter("tweetReplyTo", "${replyTweet.id}")
                appendQueryParameter("screenNameReplyTo", replyTweet.user.screenName)
            }
        }
        .build()
}
