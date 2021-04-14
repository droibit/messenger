package com.droibit.looking2.core.data.source.remote.twitter.api.list

import com.google.gson.annotations.SerializedName
import com.twitter.sdk.android.core.models.User

data class UserList(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("description") val description: String,
    @SerializedName("mode") val mode: String,
    @SerializedName("user") val user: User
)
