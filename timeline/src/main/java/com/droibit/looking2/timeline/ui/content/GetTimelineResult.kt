package com.droibit.looking2.timeline.ui.content

import androidx.annotation.StringRes
import com.droibit.looking2.core.model.tweet.Tweet

sealed class GetTimelineResult {
    sealed class FailureType {
        object Network : FailureType()
        object Limited : FailureType()
        class UnExpected(@StringRes val messageResId: Int) : FailureType()
    }

    object InProgress : GetTimelineResult()
    class Success(val timeline: List<Tweet>) : GetTimelineResult()
    class Failure(val type: FailureType) : GetTimelineResult()
}