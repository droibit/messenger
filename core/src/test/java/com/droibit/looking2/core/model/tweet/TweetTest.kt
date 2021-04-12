package com.droibit.looking2.core.model.tweet

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class TweetTest {

    @Test
    fun hasPhotoUrl() {
        kotlin.run {
            val tweet = create(medium = emptyList())
            assertThat(tweet.hasPhotoUrl).isFalse()
        }

        kotlin.run {
            val tweet = create(
                medium = listOf(
                    mock(),
                    mock<Media.Photo>(),
                    mock<Media.Unsupported>()
                )
            )
            assertThat(tweet.hasPhotoUrl).isTrue()
        }
    }

    @Test
    fun hasPhotoUrl_inRetweetedTweet() {
        kotlin.run {
            val tweet = create(
                retweetedTweet = create(medium = emptyList())
            )
            assertThat(tweet.hasPhotoUrl).isFalse()
        }

        kotlin.run {
            val tweet = create(
                retweetedTweet = create(
                    medium = listOf(
                        mock(),
                        mock<Media.Photo>(),
                        mock<Media.Unsupported>()
                    )
                )
            )
            assertThat(tweet.hasPhotoUrl).isTrue()
        }
    }

    @Test
    fun photoUrls() {
        val url1 = mock<ShorteningUrl>()
        val photo1 = mock<Media.Unsupported> { on { url } doReturn url1 }

        val url2 = mock<ShorteningUrl>()
        val photo2 = mock<Media.Photo> { on { url } doReturn url2 }

        val tweet = create(medium = listOf(photo1, photo2))
        assertThat(tweet.photoUrls).containsExactly(url1, url2)
        assertThat(tweet.quotedTweet).isNull()
    }

    @Test
    fun photoUrls_inRetweetedTweet() {
        val url1 = mock<ShorteningUrl>()
        val photo1 = mock<Media.Unsupported> { on { url } doReturn url1 }

        val url2 = mock<ShorteningUrl>()
        val photo2 = mock<Media.Photo> { on { url } doReturn url2 }

        val tweet = create(
            retweetedTweet = create(medium = listOf(photo1, photo2))
        )
        assertThat(tweet.photoUrls).containsExactly(url1, url2)
        assertThat(tweet.medium).isEmpty()
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
