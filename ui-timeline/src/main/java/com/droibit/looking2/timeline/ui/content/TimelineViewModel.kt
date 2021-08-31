package com.droibit.looking2.timeline.ui.content

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.ui.common.Event
import com.droibit.looking2.ui.common.ext.toErrorEventLiveData
import com.droibit.looking2.ui.common.ext.toSuccessLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE
import kotlinx.coroutines.launch

private typealias Timeline = List<Tweet>

@HiltViewModel
class TimelineViewModel(
    private val getTimelineCall: TimelineSource.GetCall,
    private val isLoadingSink: MutableLiveData<Boolean>,
    private val getTimelineResultSink: MutableLiveData<Result<Timeline>>
) : ViewModel() {

    private val getTimelineResult: LiveData<Result<Timeline>> by lazy(NONE) {
        viewModelScope.launch {
            getTimelineResultSink.value = try {
                isLoadingSink.value = true
                val timeline = getTimelineCall(sinceId = null)
                Result.success(timeline)
            } catch (e: TwitterError) {
                Result.failure(GetTimelineErrorMessage(source = e))
            } finally {
                isLoadingSink.value = false
            }
        }
        getTimelineResultSink
    }

    val isLoading: LiveData<Boolean>
        get() = isLoadingSink

    val timeline: LiveData<Timeline> = getTimelineResult.toSuccessLiveData()

    val isNotEmptyTimeline: LiveData<Boolean> = timeline.map { it.isNotEmpty() }

    val error: LiveData<Event<GetTimelineErrorMessage>> = getTimelineResult.toErrorEventLiveData()

    @Inject
    constructor(getTimelineCall: TimelineSource.GetCall) : this(
        getTimelineCall,
        MutableLiveData(false),
        MutableLiveData()
    )
}
