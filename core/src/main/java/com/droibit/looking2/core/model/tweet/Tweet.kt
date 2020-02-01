package com.droibit.looking2.core.model.tweet

import java.io.Serializable

data class Tweet(
    val id: Long,
    val text: String,
    val createdAt: Long,
    val urls: List<ShorteningUrl>,
    val medium: List<Media>,
    val user: User,
    val liked: Boolean,
    val retweeted: Boolean,
    val retweetedTweet: Tweet?,
    val quotedTweet: Tweet?
): Serializable {

    val hasPhotoUrl: Boolean
        get() {
            val tweet = retweetedTweet ?: this
            return tweet.medium.firstOrNull { it is Media.Photo } != null
        }

    val photoUrls: List<ShorteningUrl>
        get() {
            val tweet = retweetedTweet ?: this
            return tweet.medium.asSequence()
                .filter { it is Media.Photo }
                .map { it.url }
                .toList()
        }

    val url: String
        get() = "https://twitter.com/${user.screenName}/status/$id"
}