package com.droibit.looking2.timeline.ui.content

import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.ui.content.GetTimelineResult.FailureType
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.droibit.looking2.timeline.ui.content.GetTimelineResult.Failure as FailureResult
import com.droibit.looking2.timeline.ui.content.GetTimelineResult.Success as SuccessResult

class TimelineViewModel(
    private val getTimelineCall: GetTimelineCall,
    private val getTimelineResultSink: MutableLiveData<GetTimelineResult>
) : ViewModel(), LifecycleObserver {

    val getTimelineResult: LiveData<GetTimelineResult> get() = getTimelineResultSink

    @Inject
    constructor(getTimelineCall: GetTimelineCall) : this(getTimelineCall, MutableLiveData())

    @OnLifecycleEvent(ON_CREATE)
    fun onCreate() {
        if (getTimelineResultSink.value != null) {
            return
        }

        getTimelineResultSink.value = GetTimelineResult.InProgress
        viewModelScope.launch {
            getTimelineResultSink.value = try {
                val timeline = getTimelineCall.execute(sinceId = null)
                SuccessResult(timeline)
            } catch (e: TwitterError) {
                when (e) {
                    is TwitterError.Network -> FailureResult(FailureType.Network)
                    is TwitterError.UnExpected -> FailureResult(
                        FailureType.UnExpected(messageResId = R.string.timeline_error_obtain_timeline)
                    )
                    is TwitterError.Limited -> FailureResult(FailureType.Limited)
                    is TwitterError.Unauthorized -> TODO("Not implemented.")
                }
            }
        }
    }
}