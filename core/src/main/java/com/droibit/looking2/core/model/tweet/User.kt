package com.droibit.looking2.core.model.tweet

import java.io.Serializable

data class User(
    val id: Long,
    val name: String,
    val screenName: String,
    val profileUrl: String
): Serializable