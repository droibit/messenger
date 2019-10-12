package com.droibit.looking2.timeline.ui.content.mylist

import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.data.repository.userlist.UserListRepository
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.util.toEvent
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.ui.content.mylist.GetMyListsResult.FailureType
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.droibit.looking2.timeline.ui.content.mylist.GetMyListsResult.Failure as FailureResult
import com.droibit.looking2.timeline.ui.content.mylist.GetMyListsResult.Success as SuccessResult

class MyListsViewModel(
    private val userListRepository: UserListRepository,
    private val getMyListsResultSink: MutableLiveData<GetMyListsResult>
) : ViewModel(), LifecycleObserver {

    val getMyListsResult: LiveData<GetMyListsResult> get() = getMyListsResultSink

    @Inject
    constructor(userListRepository: UserListRepository) : this(
        userListRepository,
        MutableLiveData()
    )

    @OnLifecycleEvent(ON_CREATE)
    fun onCreate() {
        if (getMyListsResultSink.value != null) {
            return
        }

        getMyListsResultSink.value = GetMyListsResult.InProgress
        viewModelScope.launch {
            getMyListsResultSink.value = try {
                val myLists = userListRepository.getMyLists()
                SuccessResult(myLists)
            } catch (e: TwitterError) {
                val failureType = when (e) {
                    is TwitterError.Network -> FailureType.Network
                    is TwitterError.Limited -> FailureType.Limited
                    is TwitterError.UnExpected -> FailureType.UnExpected(messageResId = R.string.my_lists_error_obtain_lists)
                    is TwitterError.Unauthorized -> TODO("No implemented.")
                }
                FailureResult(failureType.toEvent())
            }
        }
    }
}