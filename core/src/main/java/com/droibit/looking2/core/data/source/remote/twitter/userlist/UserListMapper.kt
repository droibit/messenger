package com.droibit.looking2.core.data.source.remote.twitter.userlist

import com.droibit.looking2.core.data.source.remote.twitter.api.list.UserList as UserListResponse
import com.droibit.looking2.core.model.tweet.User
import com.droibit.looking2.core.model.tweet.UserList
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.User as UserResponse
import java.text.DateFormat
import java.text.ParseException
import javax.inject.Inject
import javax.inject.Named

private const val PROFILE_ICON_SIZE_NORMAL = "_normal"
private const val PROFILE_ICON_SIZE_BIGGER = "_bigger"

class UserListMapper @Inject constructor(
    @Named("twitterApi") private val dateFormat: DateFormat
) {

    @Throws(TwitterException::class)
    fun toUserLists(source: List<UserListResponse>): List<UserList> {
        try {
            return source.map { it.toUserLists() }
        } catch (e: TwitterException) {
            throw TwitterException("Parse Failure", e)
        }
    }

    @Throws(ParseException::class)
    private fun UserListResponse.toUserLists(): UserList {
        return UserList(
            id,
            name,
            description,
            createdAt = parseTime(createdAt),
            isPrivate = mode == "private",
            user = user.toUser()
        )
    }

    private fun UserResponse.toUser() = User(
        id,
        name,
        screenName,
        profileUrl = profileImageUrlHttps.replaceFirst(
            PROFILE_ICON_SIZE_NORMAL,
            PROFILE_ICON_SIZE_BIGGER
        )
    )

    @Throws(ParseException::class)
    private fun parseTime(time: String): Long {
        return requireNotNull(dateFormat.parse(time)).time
    }
}
