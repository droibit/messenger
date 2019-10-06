package com.droibit.looking2.core.data.repository.userlist.service

import com.droibit.looking2.core.model.tweet.User
import com.droibit.looking2.core.model.tweet.UserList
import java.text.DateFormat
import java.text.ParseException
import javax.inject.Inject
import javax.inject.Named
import com.droibit.looking2.core.data.source.api.twitter.list.UserList as UserListResponse
import com.twitter.sdk.android.core.models.User as UserResponse

private const val PROFILE_ICON_SIZE_NORMAL = "_normal"
private const val PROFILE_ICON_SIZE_BIGGER = "_bigger"

class UserListMapper @Inject constructor(
    @Named("twitterApi") private val dateFormat: DateFormat
) {

    @Throws(ParseException::class)
    fun toUserLists(source: List<UserListResponse>): List<UserList> {
        return source.map { it.toUserLists() }
    }

    private fun UserListResponse.toUserLists(): UserList {
        return UserList(
            id, name, description,
            createdAt = dateFormat.parse(createdAt)!!.time,
            isPrivate = mode == "private",
            user = user.toUser()
        )
    }

    private fun UserResponse.toUser() = User(
        id, name, screenName,
        profileUrl = profileImageUrlHttps.replaceFirst(
            PROFILE_ICON_SIZE_NORMAL,
            PROFILE_ICON_SIZE_BIGGER
        )
    )
}