package com.droibit.looking2.core.data.source.remote.twitter.timeline

import com.droibit.looking2.core.model.tweet.Media
import com.droibit.looking2.core.model.tweet.ShorteningUrl
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.User
import com.droibit.looking2.core.util.ext.unescapeHtml
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.TweetEntities
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject
import com.droibit.looking2.core.model.tweet.Media.Photo as PhotoMedia
import com.droibit.looking2.core.model.tweet.Media.Unsupported as UnsupportedMedia
import com.twitter.sdk.android.core.models.Tweet as TweetResponse
import com.twitter.sdk.android.core.models.User as UserResponse

// TODO: Consider using ThreadLocal when parsing dates with multithreading.
private val dateTimeRFC822 = SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH)
private const val MEDIA_TYPE_PHOTO = "photo"
private const val PROFILE_ICON_SIZE_NORMAL = "_normal"
private const val PROFILE_ICON_SIZE_BIGGER = "_bigger"

class TimelineMapper @Inject constructor() {

    @Throws(TwitterException::class)
    fun toTimeline(source: List<TweetResponse>): List<Tweet> {
        try {
            return source.map { tweet ->
                if (tweet.retweetedStatus == null) {
                    tweet.toTweet()
                } else {
                    tweet.toRetweetedTweet()
                }
            }
        } catch (e: ParseException) {
            throw TwitterException("Parse Failure", e)
        }
    }
}

@Throws(ParseException::class)
private fun TweetResponse.toTweet(): Tweet {
    return Tweet(
        id = id,
        text = text.unescapeHtml(),
        createdAt = parseTime(
            createdAt
        ),
        urls = entities.toShorteningUrls(),
        medium = extendedEntities.toMediaShorteningUrls(),
        user = user.toUser(),
        liked = favorited,
        retweeted = retweeted,
        retweetedTweet = null,
        quotedTweet = quotedStatus?.toTweet()
    )
}

@Throws(ParseException::class)
private fun TweetResponse.toRetweetedTweet(): Tweet {
    return Tweet(
        id = id,
        text = "",
        createdAt = parseTime(
            createdAt
        ),
        urls = emptyList(),
        medium = emptyList(),
        user = user.toUser(),
        liked = favorited,
        retweeted = retweeted,
        retweetedTweet = retweetedStatus.toTweet(),
        quotedTweet = quotedStatus?.toTweet()
    )
}

private fun TweetEntities?.toShorteningUrls(): List<ShorteningUrl> {
    val urls = this?.urls ?: return emptyList()
    return urls.map {
        ShorteningUrl(it.url, it.displayUrl, it.expandedUrl)
    }
}

private fun TweetEntities?.toMediaShorteningUrls(): List<Media> {
    val media = this?.media ?: return emptyList()
    return media.map {
        when (it.type) {
            MEDIA_TYPE_PHOTO -> PhotoMedia(
                ShorteningUrl(it.url, it.displayUrl, "${it.mediaUrlHttps}:small")
            )
            // e.g. `video`, `animated_gif` ...
            else -> UnsupportedMedia(
                it.type,
                ShorteningUrl(it.url, it.displayUrl, it.mediaUrlHttps)
            )
        }
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
    return requireNotNull(dateTimeRFC822.parse(time)).time
}