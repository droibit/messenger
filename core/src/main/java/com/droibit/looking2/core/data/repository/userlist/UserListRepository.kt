package com.droibit.looking2.core.data.repository.userlist

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.userlist.service.UserListService
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterStore
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.UserList
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserListRepository @Inject constructor(
    private val userListService: UserListService,
    private val localStore: LocalTwitterStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {

    @Throws(TwitterError::class)
    suspend fun getMyLists(): List<UserList> {
        return withContext(dispatcherProvider.io) {
            val session = localStore.activeSession() ?: throw TwitterError.Unauthorized
            userListService.getUserLists(session, userId = null)
        }
    }
}