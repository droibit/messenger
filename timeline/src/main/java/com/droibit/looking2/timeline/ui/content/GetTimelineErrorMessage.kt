package com.droibit.looking2.timeline.ui.content

import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.timeline.R as timelineR
import com.droibit.looking2.ui.common.StringResourceToast
import com.droibit.looking2.ui.common.ToastConvertible

// TODO: Add UnauthorizedErrorDialog
sealed class GetTimelineErrorMessage : Throwable() {
    data class Toast(
        private val value: ToastConvertible
    ) : GetTimelineErrorMessage(), ToastConvertible by value

    companion object {
        operator fun invoke(source: TwitterError): GetTimelineErrorMessage {
            return when (source) {
                is TwitterError.Network -> Toast(StringResourceToast.Network)
                is TwitterError.Limited -> Toast(StringResourceToast.RateLimited)
                is TwitterError.UnExpected -> Toast(
                    StringResourceToast(timelineR.string.timeline_error_obtain_timeline)
                )
                is TwitterError.Unauthorized -> Toast(
                    StringResourceToast.UnauthorizedTwitterAccount
                )
            }
        }
    }
}
