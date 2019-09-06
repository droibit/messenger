package com.droibit.looking2.core.data.source.api.twitter.list

import com.google.gson.annotations.SerializedName
import com.twitter.sdk.android.core.models.User

class UserList(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("description") val description: String,
    @SerializedName("mode") val mode: String,
    @SerializedName("user") val user: User
) {

    override fun toString() =
        "UserList(id=$id, name='$name', createdAt='$createdAt', description='$description', mode='$mode', user=$user)"
}