package com.droibit.looking2.tweet.ui.input

import androidx.annotation.StringRes
import com.droibit.looking2.core.util.Event

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