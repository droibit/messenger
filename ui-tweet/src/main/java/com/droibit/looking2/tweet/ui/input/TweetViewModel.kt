package com.droibit.looking2.tweet.ui.input

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.droibit.looking2.ui.common.Event
import com.droibit.looking2.ui.common.ext.requireValue
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
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
