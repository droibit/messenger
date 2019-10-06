package com.droibit.looking2.core.model.tweet

sealed class Media {

    abstract val url: ShorteningUrl

    data class Photo(override val url: ShorteningUrl) : Media()
}