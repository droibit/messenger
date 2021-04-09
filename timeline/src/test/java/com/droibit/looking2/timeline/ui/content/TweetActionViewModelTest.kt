package com.droibit.looking2.timeline.ui.content

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.droibit.looking2.core.model.tweet.ShorteningUrl
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.timeline.ui.content.TweetActionItemList.Item.LIKES
import com.droibit.looking2.timeline.ui.content.TweetActionItemList.Item.PHOTO
import com.droibit.looking2.timeline.ui.content.TweetActionItemList.Item.REPLY
import com.droibit.looking2.timeline.ui.content.TweetActionItemList.Item.RETWEET
import com.jraska.livedata.test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

class TweetActionViewModelTest {

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var tweetActionCall: TweetActionCall

    @Spy
    private var tweetActionItemListSink = MutableLiveData<Event<TweetActionItemList>>()

    @Spy
    private var replySink = MutableLiveData<Event<Tweet>>()

    @Spy
    private var photoListSink = MutableLiveData<Event<List<String>>>()

    @Spy
    private var retweetCompletedSink = MutableLiveData<Event<Unit>>()

    @Spy
    private var likesCompletedSink = MutableLiveData<Event<Unit>>()

    private lateinit var viewModel: TweetActionViewModel

    @Before
    fun setUp() {
        viewModel = TweetActionViewModel(
            tweetActionCall,
            tweetActionItemListSink,
            replySink,
            photoListSink,
            retweetCompletedSink,
            likesCompletedSink
        )
    }

    @Test
    fun tweetActionItemList() {
        val testObserver = viewModel.tweetActionItemList.test()

        val event1 = mock<Event<TweetActionItemList>>()
        tweetActionItemListSink.value = event1
        val event2 = mock<Event<TweetActionItemList>>()
        tweetActionItemListSink.value = event2

        testObserver.assertValueHistory(event1, event2)
    }

    @Test
    fun reply() {
        val testObserver = viewModel.reply.test()

        val event1 = mock<Event<Tweet>>()
        replySink.value = event1
        val event2 = mock<Event<Tweet>>()
        replySink.value = event2

        testObserver.assertValueHistory(event1, event2)
    }

    @Test
    fun photos() {
        val testObserver = viewModel.photos.test()

        val event1 = mock<Event<List<String>>>()
        photoListSink.value = event1
        val event2 = mock<Event<List<String>>>()
        photoListSink.value = event2

        testObserver.assertValueHistory(event1, event2)
    }

    @Test
    fun retweetCompleted() {
        val testObserver = viewModel.retweetCompleted.test()

        val event1 = mock<Event<Unit>>()
        retweetCompletedSink.value = event1
        val event2 = mock<Event<Unit>>()
        retweetCompletedSink.value = event2

        testObserver.assertValueHistory(event1, event2)
    }

    @Test
    fun likesCompleted() {
        val testObserver = viewModel.likesCompleted.test()

        val event1 = mock<Event<Unit>>()
        likesCompletedSink.value = event1
        val event2 = mock<Event<Unit>>()
        likesCompletedSink.value = event2

        testObserver.assertValueHistory(event1, event2)
    }

    @Test
    fun onTweetClick_minimumActionList() {
        val tweet = mock<Tweet> {
            on { this.liked } doReturn true
            on { this.retweeted } doReturn true
            on { this.hasPhotoUrl } doReturn false
        }

        val testObserver = tweetActionItemListSink.test()
        viewModel.onTweetClick(tweet)

        testObserver.assertValue(
            Event(
                TweetActionItemList(
                    tweet, items = listOf(REPLY)
                )
            )
        )
    }

    @Test
    fun onTweetClick_maximumActionList() {
        val tweet = mock<Tweet> {
            on { this.liked } doReturn false
            on { this.retweeted } doReturn false
            on { this.hasPhotoUrl } doReturn true
        }

        val testObserver = tweetActionItemListSink.test()
        viewModel.onTweetClick(tweet)

        testObserver.assertValue(
            Event(
                TweetActionItemList(
                    tweet, items = listOf(REPLY, LIKES, RETWEET, PHOTO)
                )
            )
        )
    }

    @Test
    fun onTweetActionItemClick_reply() {
        val tweet = mock<Tweet>()
        val actionItem = REPLY
        tweetActionItemListSink.value = Event(TweetActionItemList(tweet, listOf(actionItem)))

        val testObserver = replySink.test()
        viewModel.onTweetActionItemClick(actionItem)

        testObserver.assertValue(Event(tweet))
    }

    @Test
    fun onTweetActionItemClick_retweet() {
        val tweetId = Long.MAX_VALUE
        val tweet = mock<Tweet> {
            on { this.id } doReturn tweetId
        }
        val actionItem = RETWEET
        tweetActionItemListSink.value = Event(TweetActionItemList(tweet, listOf(actionItem)))

        val testObserver = retweetCompletedSink.test()
        viewModel.onTweetActionItemClick(actionItem)

        testObserver.assertValue(Event(Unit))
        verify(tweetActionCall).enqueueRetweetWork(tweetId)
        verify(tweetActionCall, never()).enqueueLikesWork(anyLong())
    }

    @Test
    fun onTweetActionItemClick_likes() {
        val tweetId = Long.MAX_VALUE
        val tweet = mock<Tweet> {
            on { this.id } doReturn tweetId
        }
        val actionItem = LIKES
        tweetActionItemListSink.value = Event(TweetActionItemList(tweet, listOf(actionItem)))

        val testObserver = likesCompletedSink.test()
        viewModel.onTweetActionItemClick(actionItem)

        testObserver.assertValue(Event(Unit))
        verify(tweetActionCall).enqueueLikesWork(tweetId)
        verify(tweetActionCall, never()).enqueueRetweetWork(anyLong())
    }

    @Test
    fun onTweetActionItemClick_photo() {
        val photoUrl1 = "photo_url1"
        val shorteningUrl1 = mock<ShorteningUrl> {
            on { this.expandedUrl } doReturn photoUrl1
        }
        val photoUrl2 = "photo_url2"
        val shorteningUrl2 = mock<ShorteningUrl> {
            on { this.expandedUrl } doReturn photoUrl2
        }
        val tweet = mock<Tweet> {
            on { this.photoUrls } doReturn listOf(shorteningUrl1, shorteningUrl2)
        }
        val actionItem = PHOTO
        tweetActionItemListSink.value = Event(TweetActionItemList(tweet, listOf(actionItem)))

        val testObserver = photoListSink.test()
        viewModel.onTweetActionItemClick(actionItem)

        testObserver.assertValue(Event(listOf(photoUrl1, photoUrl2)))
    }

    @Ignore("Not implemented yet.")
    @Test
    fun onTweetActionItemClick_addToPocket() {
    }
}