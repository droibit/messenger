package com.droibit.looking2.core.data.repository.userlist

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterSource
import com.droibit.looking2.core.data.source.remote.twitter.userlist.RemoteUserListSource
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.UserList
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.withContext

@Singleton
class UserListRepository @Inject constructor(
    private val remoteSource: RemoteUserListSource,
    private val localSource: LocalTwitterSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) {
    @Throws(TwitterError::class)
    suspend fun getMyLists(): List<UserList> {
        return withContext(dispatcherProvider.io) {
            val session = localSource.activeSession ?: throw TwitterError.Unauthorized
            remoteSource.getUserLists(session, userId = null)
        }
    }
}
