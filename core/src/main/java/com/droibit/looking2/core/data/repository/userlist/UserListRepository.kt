package com.droibit.looking2.core.data.repository.userlist

import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.UserList

interface UserListRepository {

    @Throws(TwitterError::class)
    suspend fun getMyLists(): List<UserList>
}