package com.droibit.looking2.timeline.ui.content

import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.model.tweet.GetTimelineError
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.toEvent
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.ui.content.GetTimelineResult.FailureType
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.droibit.looking2.timeline.ui.content.GetTimelineResult.Failure as FailureResult
import com.droibit.looking2.timeline.ui.content.GetTimelineResult.Success as SuccessResult

class TimelineViewModel(
    private val getTimelineCall: GetTimelineCall,
    private val getTimelineResultSink: MutableLiveData<Event<GetTimelineResult>>
) : ViewModel(), LifecycleObserver {

    private var getTimelineJob: Job? = null

    val getTimelineResult: LiveData<Event<GetTimelineResult>> get() = getTimelineResultSink

    @Inject
    constructor(getTimelineCall: GetTimelineCall) : this(getTimelineCall, MutableLiveData())

    @OnLifecycleEvent(ON_CREATE)
    fun onCreate() {
        if (getTimelineJob?.isActive == true) {
            return
        }

        getTimelineResultSink.value = GetTimelineResult.InProgress.toEvent()
        getTimelineJob = viewModelScope.launch {
            val result = try {
                val timeline = getTimelineCall.execute(sinceId = null)
                SuccessResult(timeline)
            } catch (e: GetTimelineError) {
                when (e) {
                    is GetTimelineError.Network -> FailureResult(FailureType.Network)
                    is GetTimelineError.UnExpected -> FailureResult(
                        FailureType.UnExpected(messageResId = R.string.timeline_error_obtain_timeline)
                    )
                }
            }
            getTimelineResultSink.value = result.toEvent()
        }
    }
}