package com.droibit.looking2.core.model.tweet

import java.io.Serializable

sealed class Media : Serializable {

    abstract val url: ShorteningUrl

    data class Photo(override val url: ShorteningUrl) : Media()

    /**
     * It does not support media such as videos, but retains a URL to display thumbnails.
     */
    data class Unsupported(
        val type: String,
        override val url: ShorteningUrl
    ) : Media()
}