package com.droibit.looking2.timeline.ui.content

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.toEvent
import timber.log.Timber
import javax.inject.Inject

class TweetActionViewModel(
    private val tweetActionCall: TweetActionCall,
    private val tweetActionSink: MutableLiveData<Event<TweetAction>>
) : ViewModel() {

    val tweetAction: LiveData<Event<TweetAction>>
        get() = tweetActionSink

    @Inject
    constructor(tweetActionCall: TweetActionCall) : this(tweetActionCall, MutableLiveData())

    @UiThread
    fun onTweetClick(tweet: Tweet) {
        val items = mutableListOf(TweetAction.Item.REPLY).apply {
            if (!tweet.liked) {
                add(TweetAction.Item.LIKES)
            }
            if (!tweet.retweeted) {
                add(TweetAction.Item.RETWEET)
            }
            if (tweet.hasPhotoUrl) {
                add(TweetAction.Item.PHOTO)
            }
        }
        tweetActionSink.value = TweetAction(target = tweet, items = items).toEvent()
    }

    fun onTweetActionItemClick(actionItem: TweetAction.Item) {
        Timber.d("Clicked item: $actionItem")
        val targetTweet = requireNotNull(tweetActionSink.value).peek().target
        when (actionItem) {
            TweetAction.Item.REPLY -> {
            }
            TweetAction.Item.RETWEET -> {
                tweetActionCall.enqueueRetweetWork(targetTweet.id)
                // TODO: show in progress message.
            }
            TweetAction.Item.LIKES -> {
                tweetActionCall.enqueueLikesWork(targetTweet.id)
            }
            TweetAction.Item.PHOTO -> {
            }
            TweetAction.Item.ADD_TO_POCKET -> TODO()
        }
    }
}