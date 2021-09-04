package com.droibit.looking2.tweet.ui

import androidx.annotation.Keep
import androidx.lifecycle.SavedStateHandle
import java.io.Serializable

@Keep
data class ReplyTweet(
    val id: Long,
    val screenName: String
) : Serializable {

    companion object {
        private const val KEY_TWEET_REPLY_TO = "tweetReplyTo"
        private const val KEY_SCREEN_NAME_REPLY_TO = "screenNameReplyTo"
        private const val KEY_REPLY_TWEET = "replyTweet"

        operator fun invoke(savedState: SavedStateHandle): ReplyTweet? {
            val replyTweet = savedState.get<ReplyTweet>(KEY_REPLY_TWEET)
            if (replyTweet != null) {
                return replyTweet
            }

            val tweetReplyTo = savedState.get<String>(KEY_TWEET_REPLY_TO) ?: return null
            val screenNameReplyTo = savedState.get<String>(KEY_SCREEN_NAME_REPLY_TO) ?: return null
            return ReplyTweet(tweetReplyTo.toLong(), screenNameReplyTo)
        }
    }
}
