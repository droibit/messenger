package com.droibit.looking2.timeline.ui.content

import androidx.annotation.IdRes
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.timeline.R

data class TweetActionItemList(
    val target: Tweet,
    val items: List<Item>
) {
    enum class Item(@IdRes val id: Int) {
        REPLY(R.id.tweet_action_reply),
        RETWEET(R.id.tweet_action_retweet),
        LIKES(R.id.tweet_action_likes),
        PHOTO(R.id.tweet_action_show_photo),
        ADD_TO_POCKET(R.id.tweet_action_add_pocket);

        companion object {
            operator fun invoke(@IdRes id: Int) = values().first { it.id == id }
        }
    }
}
