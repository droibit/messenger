package com.droibit.looking2.core.data.source.api.twitter

import com.droibit.looking2.core.data.source.api.twitter.list.UserList
import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.core.models.User
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

class LookingTwitterApiClient(
    session: TwitterSession,
    client: OkHttpClient
) : TwitterApiClient(session, client) {

    interface UserListService {

        @GET("/1.1/lists/list.json")
        fun list(
            @Query("user_id") userId: Long?,
            @Query("screen_name") screenName: String?,
            @Query("reverse") reverse: Boolean?
        ): Call<List<UserList>>

        @GET(
            "/1.1/lists/statuses.json?"
                + "tweet_mode=extended&include_cards=true&cards_platform=TwitterKit-13"
        )
        fun statuses(
            @Query("list_id") listId: Long?,
            @Query("slug") slug: String?,
            @Query("owner_screen_name") ownerScreenName: String?,
            @Query("owner_id") ownerId: Long?,
            @Query("since_id") sinceId: Long?,
            @Query("max_id") maxId: Long?,
            @Query("count") count: Int?,
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

    val userListService: UserListService get() = getService(UserListService::class.java)

    val userService: UserService get() = getService(UserService::class.java)

    @Throws(TwitterException::class)
    suspend fun postTweet(
        text: String,
        inReplyToId: Long?
    ): Tweet {
        return statusesService.update(
            text,
            inReplyToId,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )
            .executeInternal()
    }

    @Throws(TwitterException::class)
    suspend fun retweet(tweetId: Long): Tweet = statusesService.retweet(
        tweetId,
        null
    ).executeInternal()

    @Throws(TwitterException::class)
    suspend fun likeTweet(tweetId: Long): Tweet {
        return favoriteService.create(tweetId, null)
            .executeInternal()
    }

    @Throws(TwitterException::class)
    suspend fun fetchHomeTimeline(
        count: Int,
        sinceId: Long?
    ): List<Tweet> {
        return statusesService.homeTimeline(
            count,
            sinceId,
            null,
            null,
            null,
            null,
            null
        )
            .executeInternal()
    }

    @Throws(TwitterException::class)
    suspend fun fetchUserLists(): List<UserList> {
        return userListService.list(null, null, null)
            .executeInternal()
    }

    @Throws(TwitterException::class)
    suspend fun fetchUserListTimeline(
        listId: Long,
        count: Int,
        sinceId: Long?
    ): List<Tweet> {
        return userListService.statuses(
            listId,
            null,
            null,
            null, sinceId,
            null, count,
            null,
            null
        )
            .executeInternal()
    }

    @Throws(TwitterException::class)
    suspend fun fetchMentionsTimeline(
        count: Int,
        sinceId: Long?
    ): List<Tweet> {
        return statusesService.mentionsTimeline(
            count,
            sinceId,
            null,
            null,
            null,
            null
        )
            .executeInternal()
    }
}