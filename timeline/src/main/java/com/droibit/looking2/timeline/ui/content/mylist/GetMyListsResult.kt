package com.droibit.looking2.timeline.ui.content.mylist

import androidx.annotation.StringRes
import com.droibit.looking2.core.model.tweet.UserList
import com.droibit.looking2.core.util.Event

sealed class GetMyListsResult {
    sealed class FailureType {
        object Network : FailureType()
        object Limited : FailureType()
        class UnExpected(@StringRes val messageResId: Int) : FailureType()
    }

    object InProgress : GetMyListsResult()
    class Success(val myLists: List<UserList>) : GetMyListsResult()
    class Failure(val type: Event<FailureType>) : GetMyListsResult()
}