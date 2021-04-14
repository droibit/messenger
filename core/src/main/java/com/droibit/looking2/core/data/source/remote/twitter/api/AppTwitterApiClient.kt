package com.droibit.looking2.core.data.source.remote.twitter.api

import com.droibit.looking2.core.data.source.remote.twitter.api.list.UserList
import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

class AppTwitterApiClient(
    session: TwitterSession,
    client: OkHttpClient
) : TwitterApiClient(session, client) {

    interface Factory {
        fun get(session: TwitterSession): AppTwitterApiClient
    }

    interface UserListService {

        @GET("/1.1/lists/list.json")
        fun list(
            @Query("user_id") userId: Long?,
            @Query("screen_name") screenName: String?,
            @Query("reverse") reverse: Boolean?
        ): Call<List<UserList>>

        @GET(
            "/1.1/lists/statuses.json?tweet_mode=extended&include_cards=true" +
                "&cards_platform=TwitterKit-13"
        )
        fun statuses(
            @Query("list_id") listId: Long?,
            @Query("count") count: Int?,
            @Query("since_id") sinceId: Long?,
            @Query("slug") slug: String?,
            @Query("owner_screen_name") ownerScreenName: String?,
            @Query("owner_id") ownerId: Long?,
            @Query("max_id") maxId: Long?,
            @Query("include_entities") includeEntities: Boolean?,
            @Query("include_rts") includeRts: Boolean?
        ): Call<List<Tweet>>
    }

    interface UserService {

        @GET("/1.1/users/show.json")
        fun show(
            @Query("user_id") userId: Long?,
            @Query("screen_name") screenName: String?,
            @Query("include_entities") includeEntities: Boolean?
        ): Call<User>
    }

    val userListService: UserListService
        get() = getService(
            UserListService::class.java
        )

    val userService: UserService
        get() = getService(
            UserService::class.java
        )
}
