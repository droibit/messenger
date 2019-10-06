package com.droibit.looking2.core.data.repository.timeline.service

import com.droibit.looking2.core.model.tweet.Media
import com.droibit.looking2.core.model.tweet.ShorteningUrl
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.User
import com.droibit.looking2.core.util.ext.unescapeHtml
import com.twitter.sdk.android.core.models.MediaEntity
import com.twitter.sdk.android.core.models.UrlEntity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Locale
import javax.inject.Inject
import com.droibit.looking2.core.model.tweet.Media.Photo as PhotoMedia
import com.twitter.sdk.android.core.models.Tweet as TweetResponse
import com.twitter.sdk.android.core.models.User as UserResponse

// TODO: Consider using ThreadLocal when parsing dates with multithreading.
private val dateTimeRFC822 = SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH)
private const val MEDIA_TYPE_PHOTO = "photo"
private const val PROFILE_ICON_SIZE_NORMAL = "_normal"
private const val PROFILE_ICON_SIZE_BIGGER = "_bigger"

class TimelineMapper @Inject constructor() {

    @Throws(ParseException::class)
    fun toTimeline(source: List<TweetResponse>): List<Tweet> {
        return source.map { tweet ->
            if (tweet.retweetedStatus == null) {
                tweet.toTweet()
            } else {
                tweet.toRetweetedTweet()
            }
        }
    }
}

private fun TweetResponse.toTweet(): Tweet {
    return Tweet(
        id = id,
        text = text.unescapeHtml(),
        createdAt = parseTime(createdAt),
        urls = entities?.urls?.map { it.toShortUrl() } ?: emptyList(),
        medium = extendedEntities?.media?.mapNotNull { it.toMedia() } ?: emptyList(),
        user = user.toUser(),
        liked = favorited,
        retweeted = retweeted,
        retweetedTweet = null,
        quotedTweet = quotedStatus?.toTweet()
    )
}

private fun TweetResponse.toRetweetedTweet(): Tweet {
    return Tweet(
        id = id,
        text = "",
        createdAt = parseTime(createdAt),
        urls = Collections.emptyList(),
        medium = Collections.emptyList(),
        user = user.toUser(),
        liked = favorited,
        retweeted = retweeted,
        retweetedTweet = retweetedStatus.toTweet(),
        quotedTweet = quotedStatus?.toTweet()

    )
}

private fun UrlEntity.toShortUrl(): ShorteningUrl {
    return ShorteningUrl(url, displayUrl, expandedUrl)
}

private fun MediaEntity.toMedia(): Media? {
    return when (type) {
        MEDIA_TYPE_PHOTO -> {
            PhotoMedia(url = ShorteningUrl(url, displayUrl, "$mediaUrlHttps:small"))
        }
        else -> null
    }
}

private fun UserResponse.toUser() = User(
    id, name, screenName,
    profileUrl = profileImageUrlHttps.replaceFirst(
        PROFILE_ICON_SIZE_NORMAL,
        PROFILE_ICON_SIZE_BIGGER
    )
)

@Throws(ParseException::class)
private fun parseTime(time: String): Long {
    return dateTimeRFC822.parse(time)!!.time
}