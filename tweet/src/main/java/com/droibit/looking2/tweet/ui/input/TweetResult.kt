package com.droibit.looking2.tweet.ui.input

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.ext.showLongToast
import com.droibit.looking2.core.util.ext.showNetworkErrorToast
import com.droibit.looking2.core.util.ext.showShortToast

inline class SuccessfulMessage(@StringRes val resId: Int)

sealed class TweetResult {
    sealed class FailureType: Error() {
        object Network : FailureType()
        class UnExpected(@StringRes val messageResId: Int) : FailureType()
    }

    object InProgress : TweetResult()
    class Success(val message: Event<SuccessfulMessage>): TweetResult()
    class Failure(val type: Event<FailureType>) : TweetResult()
}

fun Fragment.showTweetSuccessful(message: SuccessfulMessage) {
    showShortToast(message.resId)
    requireActivity().finish()
}

fun Fragment.showTweetFailure(failureType: TweetResult.FailureType) {
    when (failureType) {
        is TweetResult.FailureType.Network -> showNetworkErrorToast()
        is TweetResult.FailureType.UnExpected -> showLongToast(failureType.messageResId)
    }
    findNavController().popBackStack()
}