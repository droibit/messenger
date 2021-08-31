package com.droibit.looking2.timeline.ui.content.mylist

import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.timeline.R
import com.droibit.looking2.ui.common.StringResourceToast
import com.droibit.looking2.ui.common.ToastConvertible

// TODO: Add UnauthorizedErrorDialog
sealed class GetMyListsErrorMessage : Throwable() {
    data class Toast(
        private val value: ToastConvertible
    ) : GetMyListsErrorMessage(), ToastConvertible by value

    companion object {
        operator fun invoke(source: TwitterError): GetMyListsErrorMessage {
            return when (source) {
                is TwitterError.Network -> Toast(StringResourceToast.Network)
                is TwitterError.Limited -> Toast(StringResourceToast.RateLimited)
                is TwitterError.UnExpected -> Toast(
                    StringResourceToast(R.string.my_lists_error_obtain_lists)
                )
                is TwitterError.Unauthorized -> Toast(
                    StringResourceToast.UnauthorizedTwitterAccount
                )
            }
        }
    }
}
