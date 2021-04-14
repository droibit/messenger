package com.droibit.looking2.tweet.ui.input

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.ext.requireValue
import javax.inject.Inject
import timber.log.Timber

class TweetViewModel(
    val tweetText: MutableLiveData<String>,
    private val tweetCall: TweetCall,
    private val tweetCompletedSink: MutableLiveData<Event<Unit>>
) : ViewModel() {

    val tweetCompleted: LiveData<Event<Unit>> get() = tweetCompletedSink

    @Inject
    constructor(call: TweetCall) : this(
        MutableLiveData(""),
        call,
        MutableLiveData()
    )

    @UiThread
    fun tweet() {
        val text = tweetText.requireValue()
        check(text.isNotEmpty())
        Timber.d("text: $text")

        tweetCall.enqueue(text)
        tweetCompletedSink.value = Event(Unit)
    }
}
