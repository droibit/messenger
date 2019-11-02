package com.droibit.looking2.core.model.tweet

import java.io.Serializable

data class UserList(
    val id: Long,
    val name: String,
    val description: String,
    val createdAt: Long,
    val isPrivate: Boolean,
    val user: User
) : Serializable