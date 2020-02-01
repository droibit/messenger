package com.droibit.looking2.core.model.tweet

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.Test

class TweetTest {

    @Test
    fun hasPhotoUrl() {
        kotlin.run {
            val tweet = create(medium = listOf(mock(), mock()))
            assertThat(tweet.hasPhotoUrl).isFalse()
        }

        kotlin.run {
            val tweet = create(medium = listOf(mock(), mock<Media.Photo>(), mock()))
            assertThat(tweet.hasPhotoUrl).isTrue()
        }
    }

    @Test
    fun hasPhotoUrl_inRetweetedTweet() {
        kotlin.run {
            val tweet = create(
                retweetedTweet = create(medium = listOf(mock(), mock()))
            )
            assertThat(tweet.hasPhotoUrl).isFalse()
        }

        kotlin.run {
            val tweet = create(
                retweetedTweet = create(medium = listOf(mock(), mock<Media.Photo>(), mock()))
            )
            assertThat(tweet.hasPhotoUrl).isTrue()
        }
    }

    @Test
    fun photoUrls() {
        val url1 = mock<ShorteningUrl>()
        val photo1 = mock<Media.Photo> { on { url } doReturn url1 }

        val url2 = mock<ShorteningUrl>()
        val photo2 = mock<Media.Photo> { on { url } doReturn url2 }

        val tweet = create(medium = listOf<Media>(mock(), photo1, photo2))
        assertThat(tweet.photoUrls).containsExactly(url1, url2)
    }

    @Test
    fun photoUrls_inRetweetedTweet() {
        val url1 = mock<ShorteningUrl>()
        val photo1 = mock<Media.Photo> { on { url } doReturn url1 }

        val url2 = mock<ShorteningUrl>()
        val photo2 = mock<Media.Photo> { on { url } doReturn url2 }

        val tweet = create(
            retweetedTweet = create(medium = listOf<Media>(photo1, mock(), photo2))
        )
        assertThat(tweet.photoUrls).containsExactly(url1, url2)
    }
}

private fun create(
    id: Long = 1L,
    text: String = "tweet_text",
    createdAt: Long = 2L,
    urls: List<ShorteningUrl> = emptyList(),
    medium: List<Media> = emptyList(),
    user: User = User(3L, "test", "screen_test", "profile"),
    liked: Boolean = true,
    retweeted: Boolean = true,
    retweetedTweet: Tweet? = null,
    quotedTweet: Tweet? = null
): Tweet {
    return Tweet(
        id,
        text,
        createdAt,
        urls,
        medium,
        user,
        liked,
        retweeted,
        retweetedTweet,
        quotedTweet
    )
}