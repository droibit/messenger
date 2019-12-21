package com.droibit.looking2.tweet.ui.input

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.toEvent
import timber.log.Timber
import javax.inject.Inject

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
        val text = tweetText.value
        check(!text.isNullOrEmpty())
        Timber.d("text: $text")

        tweetCall.enqueue(text)
        tweetCompletedSink.value = Unit.toEvent()
    }
}