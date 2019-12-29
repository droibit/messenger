package com.droibit.looking2.timeline.ui.content

import androidx.annotation.StringRes
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.timeline.R

sealed class GetTimelineError : Throwable() {
    object Network : GetTimelineError()
    object Limited : GetTimelineError()
    class UnExpected(@StringRes val messageResId: Int) : GetTimelineError()

    companion object {
        operator fun invoke(source: TwitterError): GetTimelineError {
            return when (source) {
                is TwitterError.Network -> GetTimelineError.Network
                is TwitterError.UnExpected -> GetTimelineError.UnExpected(messageResId = R.string.timeline_error_obtain_timeline)
                is TwitterError.Limited -> GetTimelineError.Limited
                is TwitterError.Unauthorized -> TODO("Not implemented.")
            }
        }
    }
}