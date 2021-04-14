package com.droibit.looking2.core.model.tweet

import java.io.Serializable

data class ShorteningUrl(
    val url: String,
    val displayUrl: String,
    val expandedUrl: String
) : Serializable
