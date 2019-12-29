package com.droibit.looking2.timeline.ui.content

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.ext.requireValue
import timber.log.Timber
import javax.inject.Inject
import com.droibit.looking2.timeline.ui.content.TweetActionItemList.Item as TweetActionItem

class TweetActionViewModel(
    private val tweetActionCall: TweetActionCall,
    private val tweetActionItemListSink: MutableLiveData<Event<TweetActionItemList>>,
    private val replySink: MutableLiveData<Event<Tweet>>,
    private val photoListSink: MutableLiveData<Event<List<String>>>,
    private val retweetCompletedSink: MutableLiveData<Event<Unit>>,
    private val likesCompletedSink: MutableLiveData<Event<Unit>>
) : ViewModel() {

    val tweetActionItemList: LiveData<Event<TweetActionItemList>>
        get() = tweetActionItemListSink

    val reply: LiveData<Event<Tweet>>
        get() = replySink

    val photos: LiveData<Event<List<String>>>
        get() = photoListSink

    val retweetCompleted: LiveData<Event<Unit>>
        get() = retweetCompletedSink

    val likesCompleted: LiveData<Event<Unit>>
        get() = likesCompletedSink

    @Inject
    constructor(tweetActionCall: TweetActionCall) : this(
        tweetActionCall,
        MutableLiveData(),
        MutableLiveData(),
        MutableLiveData(),
        MutableLiveData(),
        MutableLiveData()
    )

    @UiThread
    fun onTweetClick(tweet: Tweet) {
        val items = mutableListOf(TweetActionItem.REPLY).apply {
            if (!tweet.liked) {
                add(TweetActionItem.LIKES)
            }
            if (!tweet.retweeted) {
                add(TweetActionItem.RETWEET)
            }
            if (tweet.hasPhotoUrl) {
                add(TweetActionItem.PHOTO)
            }
        }
        tweetActionItemListSink.value = Event(TweetActionItemList(target = tweet, items = items))
    }

    @UiThread
    fun onTweetActionItemClick(actionItem: TweetActionItem) {
        Timber.d("Clicked item: $actionItem")
        val targetTweet = tweetActionItemListSink.requireValue().peek().target
        when (actionItem) {
            TweetActionItem.REPLY -> {
                replySink.value = Event(targetTweet)
            }
            TweetActionItem.RETWEET -> {
                tweetActionCall.enqueueRetweetWork(targetTweet.id)
                retweetCompletedSink.value = Event(Unit)
            }
            TweetActionItem.LIKES -> {
                tweetActionCall.enqueueLikesWork(targetTweet.id)
                likesCompletedSink.value = Event(Unit)
            }
            TweetActionItem.PHOTO -> {
                val urls = targetTweet.photoUrls.map { it.expandedUrl }
                photoListSink.value = Event(urls)
            }
            TweetActionItem.ADD_TO_POCKET -> TODO()
        }
    }
}