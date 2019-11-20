package com.droibit.looking2.timeline.ui.content.mylist

import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.data.repository.userlist.UserListRepository
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.util.toEvent
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.ui.content.mylist.GetMyListsResult.FailureType
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE
import com.droibit.looking2.timeline.ui.content.mylist.GetMyListsResult.Failure as FailureResult
import com.droibit.looking2.timeline.ui.content.mylist.GetMyListsResult.Success as SuccessResult

class MyListsViewModel(
    private val userListRepository: UserListRepository,
    private val getMyListsResultSink: MutableLiveData<GetMyListsResult>
) : ViewModel() {

    @get:UiThread
    val getMyListsResult: LiveData<GetMyListsResult> by lazy(NONE) {
        getMyListsResultSink.value = GetMyListsResult.InProgress
        viewModelScope.launch {
            getMyListsResultSink.value = try {
                val myLists = userListRepository.getMyLists()
                SuccessResult(myLists)
            } catch (e: TwitterError) {
                val failureType = when (e) {
                    is TwitterError.Network -> FailureType.Network
                    is TwitterError.Limited -> FailureType.Limited
                    is TwitterError.UnExpected -> FailureType.UnExpected(
                        messageResId = R.string.my_lists_error_obtain_lists
                    )
                    is TwitterError.Unauthorized -> TODO("No implemented.")
                }
                FailureResult(failureType.toEvent())
            }
        }
        getMyListsResultSink
    }

    @Inject
    constructor(userListRepository: UserListRepository) : this(
        userListRepository,
        MutableLiveData()
    )
}