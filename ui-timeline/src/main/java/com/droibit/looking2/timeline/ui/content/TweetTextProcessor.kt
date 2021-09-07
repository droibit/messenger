package com.droibit.looking2.timeline.ui.content

import android.text.SpannableString
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextPaint
import android.text.style.URLSpan
import android.widget.TextView
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.util.ext.unescapeRegex
import com.twitter.Regex as TwitterRegex
import javax.inject.Inject

private val highlightPatterns = listOf(
    TwitterRegex.VALID_HASHTAG,
    TwitterRegex.VALID_MENTION_OR_LIST
)

class TweetTextProcessor @Inject constructor() {

    fun processTweetText(
        view: TextView,
        tweet: Tweet
    ) {
        val quotedTweetUrl = tweet.quotedTweet?.tweetUrl
        val replaceUrls = ArrayList<Pair<String, String>>()
        tweet.urls.mapTo(replaceUrls) { shorteningUrl ->
            if (quotedTweetUrl == null) shorteningUrl.url to shorteningUrl.displayUrl else {
                // Remove quoted tweet URL.
                // NOTE: User name of URL make from "Tweet" is upper case, but user name of expanded URL may be lower case.
                val quotedTweetShorteningUrl = tweet.urls.firstOrNull {
                    it.expandedUrl.equals(quotedTweetUrl, ignoreCase = true)
                }
                // Delete quote tweet URL from text.
                shorteningUrl.url to
                    if (quotedTweetShorteningUrl == null) shorteningUrl.displayUrl else ""
            }
        }
        // Display photo icon instead of URL.
        tweet.medium.mapTo(replaceUrls) { it.url.url to "" }
        applyUrlReplacement(view, tweet.text, replaceUrls)
    }

    fun processQuotedTweetText(
        view: TextView,
        quotedTweet: Tweet
    ) {
        val replaceUrls = ArrayList<Pair<String, String>>()
        quotedTweet.urls.mapTo(replaceUrls) { it.url to it.displayUrl }
        quotedTweet.medium.mapTo(replaceUrls) { it.url.url to "" }

        applyUrlReplacement(view, quotedTweet.text, replaceUrls)
    }

    private fun applyUrlReplacement(
        view: TextView,
        srcTweetText: String,
        replaceUrls: List<Pair<String, String>>
    ) {
        var tweetText = srcTweetText
        val displayUrls = ArrayList<String>(replaceUrls.size)
        replaceUrls.forEach { (url, displayUrl) ->
            if (displayUrl.isNotEmpty()) {
                displayUrls.add(displayUrl.unescapeRegex())
            }
            tweetText = tweetText.replace(url, displayUrl)
        }

        val patterns = if (displayUrls.isEmpty()) highlightPatterns else {
            ArrayList(highlightPatterns).apply {
                add(displayUrls.joinToString("|").toPattern())
            }
        }
        view.text = if (tweetText.isBlank()) "" else {
            SpannableString.valueOf(tweetText)
                .apply {
                    patterns.forEach {
                        val m = it.matcher(tweetText)
                        while (m.find()) {
                            val span = URLSpanNoUnderline(url = m.group())
                            this.setSpan(span, m.start(), m.end(), SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                    }
                }
        } // if (...
    }

    private class URLSpanNoUnderline(url: String) : URLSpan(url) {

        override fun updateDrawState(ds: TextPaint) {
            ds.color = ds.linkColor
            ds.isUnderlineText = false
        }
    }
}