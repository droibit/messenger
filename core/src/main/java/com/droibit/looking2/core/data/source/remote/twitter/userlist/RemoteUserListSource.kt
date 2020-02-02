package com.droibit.looking2.core.data.source.remote.twitter.userlist

import com.droibit.looking2.core.data.source.remote.twitter.api.AppTwitterApiClient
import com.droibit.looking2.core.data.source.remote.twitter.api.AppTwitterApiClientFactoryDelegate
import com.droibit.looking2.core.data.source.remote.twitter.api.await
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.UserList
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import javax.inject.Inject

class RemoteUserListSource @Inject constructor(
    twitterCore: TwitterCore,
    private val mapper: UserListMapper
) : AppTwitterApiClient.Factory by AppTwitterApiClientFactoryDelegate(
    twitterCore
) {

    @Throws(TwitterError::class)
    suspend fun getUserLists(session: TwitterSession, userId: Long?): List<UserList> {
        val apiClient = get(session)
        try {
            val userListsResponse = apiClient.userListService.list(userId, null, null).await()
            return mapper.toUserLists(source = userListsResponse)
        } catch (e: TwitterException) {
            Timber.e(e)
            throw TwitterError(e)
        }
    }
}