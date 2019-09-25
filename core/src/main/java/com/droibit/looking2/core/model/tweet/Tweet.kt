package com.droibit.looking2.core.model.tweet

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
) {

    val hasPhotoUrl: Boolean
        get() {
            val tweet = retweetedTweet ?: this
            return tweet.medium.firstOrNull { it is Media.Photo } != null
        }

    val includeTweet: Boolean
        get() {
            val tweet = retweetedTweet ?: quotedTweet ?: this
            return tweet.urls.isNotEmpty()
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

    val shareUrl: ShorteningUrl
        get() {
            val tweet = this.retweetedTweet ?: this
            // Ignore quoted tweet url.
            val url = tweet.urls.firstOrNull { it.expandedUrl != quotedTweet?.url }
            if (url != null) {
                return url
            }
            return quotedTweet?.urls?.firstOrNull() ?: error("Not found share url.")
        }

    data class User(
        val id: Long,
        val name: String,
        val screenName: String,
        val profileUrl: String
    )

    sealed class Media {

        abstract val url: ShorteningUrl

        data class Photo(override val url: ShorteningUrl) : Media()
    }
}