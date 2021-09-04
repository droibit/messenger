package com.droibit.looking2.tweet.ui

import android.content.Context
import com.droibit.looking2.tweet.R
import dagger.hilt.android.scopes.FragmentScoped

@FragmentScoped
data class TweetLayoutString(
    val title: String,
    val replyUser: String,
    val tweetTextHint: String
) {
    companion object {
        operator fun invoke(context: Context, replyTweet: ReplyTweet?): TweetLayoutString {
            return if (replyTweet == null) {
                TweetLayoutString(
                    title = context.getString(R.string.tweet_title_tweet),
                    replyUser = "",
                    tweetTextHint = context.getString(R.string.tweet_text_tweet_hint)
                )
            } else {
                TweetLayoutString(
                    title = context.getString(R.string.tweet_title_reply),
                    replyUser = "@${replyTweet.screenName}",
                    tweetTextHint = context.getString(R.string.tweet_text_reply_hint)
                )
            }
        }
    }
}
