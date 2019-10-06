package com.droibit.looking2.core.data.repository.userlist.service

import com.droibit.looking2.core.data.source.api.twitter.AppTwitterApiClient
import com.droibit.looking2.core.data.source.api.twitter.AppTwitterApiClientFactoryDelegate
import com.droibit.looking2.core.data.source.api.twitter.await
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.UserList
import com.droibit.looking2.core.model.tweet.toTwitterError
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import javax.inject.Inject

class UserListService @Inject constructor(
    twitterCore: TwitterCore,
    private val mapper: UserListMapper
) : AppTwitterApiClient.Factory by AppTwitterApiClientFactoryDelegate(twitterCore) {

    @Throws(TwitterError::class)
    suspend fun getUserLists(session: TwitterSession): List<UserList> {
        val apiClient = get(session)
        try {
            val userListsResponse = apiClient.userListService.list(null, null, null).await()
            return mapper.toUserLists(source = userListsResponse)
        } catch (e: Exception) {
            Timber.e(e)
            throw e.toTwitterError()
        }
    }
}