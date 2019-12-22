package com.droibit.looking2.timeline.ui.content

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.util.toEvent
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.ui.content.GetTimelineResult.FailureType
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE
import com.droibit.looking2.timeline.ui.content.GetTimelineResult.Failure as FailureResult
import com.droibit.looking2.timeline.ui.content.GetTimelineResult.Success as SuccessResult

class TimelineViewModel(
    private val getTimelineCall: TimelineSource.GetCall,
    private val getTimelineResultSink: MutableLiveData<GetTimelineResult>
) : ViewModel() {

    @get:UiThread
    val getTimelineResult: LiveData<GetTimelineResult> by lazy(NONE) {
        viewModelScope.launch {
            getTimelineResultSink.value = try {
                val timeline = getTimelineCall(sinceId = null)
                SuccessResult(timeline)
            } catch (e: TwitterError) {
                val failureType = when (e) {
                    is TwitterError.Network -> FailureType.Network
                    is TwitterError.UnExpected -> FailureType.UnExpected(messageResId = R.string.timeline_error_obtain_timeline)
                    is TwitterError.Limited -> FailureType.Limited
                    is TwitterError.Unauthorized -> TODO("Not implemented.")
                }
                FailureResult(failureType.toEvent())
            }
        }
        getTimelineResultSink
    }

    @Inject
    constructor(getTimelineCall: TimelineSource.GetCall) : this(
        getTimelineCall,
        MutableLiveData(GetTimelineResult.InProgress)
    )
}