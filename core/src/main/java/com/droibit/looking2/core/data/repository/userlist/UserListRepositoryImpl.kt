package com.droibit.looking2.core.data.repository.userlist

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.userlist.service.UserListService
import com.droibit.looking2.core.data.source.local.twitter.TwitterLocalStore
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.UserList
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class UserListRepositoryImpl @Inject constructor(
    private val userListService: UserListService,
    private val localStore: TwitterLocalStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : UserListRepository {

    override suspend fun getMyLists(): List<UserList> {
        return withContext(dispatcherProvider.io) {
            val session = localStore.activeSession() ?: throw TwitterError.Unauthorized
            userListService.getUserLists(session, userId = null)
        }
    }
}