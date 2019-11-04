package com.droibit.looking2.tweet.ui.input

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.util.toEvent
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class TweetViewModel(
    val tweetText: MutableLiveData<String>,
    private val tweetCall: TweetCall,
    private val tweetResultSink: MutableLiveData<TweetResult>
) : ViewModel() {

    val tweetResult: LiveData<TweetResult> get() = tweetResultSink

    @Inject
    constructor(call: TweetCall) : this(MutableLiveData(""), call, MutableLiveData())

    @UiThread
    fun tweet() {
        if (tweetResult.value is TweetResult.InProgress) {
            return
        }

        viewModelScope.launch {
            val text = tweetText.value
            check(!text.isNullOrEmpty())
            Timber.d("text: $text")

            tweetResultSink.value = TweetResult.InProgress
            tweetResultSink.value = try {
                TweetResult.Success(tweetCall.call(text).toEvent())
            } catch (e: TweetResult.FailureType) {
                TweetResult.Failure(e.toEvent())
            }
        }
    }
}