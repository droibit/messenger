package com.droibit.looking2.timeline.ui.content.mylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.droibit.looking2.core.data.repository.userlist.UserListRepository
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.UserList
import com.droibit.looking2.core.util.Event
import com.droibit.looking2.core.util.ext.toErrorEventLiveData
import com.droibit.looking2.core.util.ext.toSuccessLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE
import kotlinx.coroutines.launch

private typealias UserLists = List<UserList>

@HiltViewModel
class MyListsViewModel(
    private val userListRepository: UserListRepository,
    private val isLoadingSink: MutableLiveData<Boolean>,
    private val getMyListsResultSink: MutableLiveData<Result<UserLists>>
) : ViewModel() {

    private val getMyListsResult: LiveData<Result<UserLists>> by lazy(NONE) {
        viewModelScope.launch {
            getMyListsResultSink.value = try {
                isLoadingSink.value = true
                val myLists = userListRepository.getMyLists()
                Result.success(myLists)
            } catch (e: TwitterError) {
                Result.failure(GetMyListsErrorMessage(source = e))
            } finally {
                isLoadingSink.value = false
            }
        }
        getMyListsResultSink
    }

    val isLoading: LiveData<Boolean>
        get() = isLoadingSink

    val myLists: LiveData<UserLists> = getMyListsResult.toSuccessLiveData()

    val isNotEmptyMyLists: LiveData<Boolean> = myLists.map { it.isNotEmpty() }

    val error: LiveData<Event<GetMyListsErrorMessage>> = getMyListsResult.toErrorEventLiveData()

    @Inject
    constructor(userListRepository: UserListRepository) : this(
        userListRepository,
        isLoadingSink = MutableLiveData(false),
        getMyListsResultSink = MutableLiveData()
    )
}
