package com.droibit.looking2.timeline.ui.content.mylist

import androidx.annotation.StringRes
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.timeline.R

sealed class GetMyListsError : Throwable() {
    object Network : GetMyListsError()
    object Limited : GetMyListsError()
    class UnExpected(@StringRes val messageResId: Int) : GetMyListsError()

    companion object {

        operator fun invoke(source: TwitterError): GetMyListsError {
            return when (source) {
                is TwitterError.Network -> Network
                is TwitterError.Limited -> Limited
                is TwitterError.UnExpected -> UnExpected(
                    messageResId = R.string.my_lists_error_obtain_lists
                )
                is TwitterError.Unauthorized -> TODO("No implemented.")
            }
        }
    }
}
