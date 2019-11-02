package com.droibit.looking2.core.model.tweet

import java.io.Serializable

sealed class Media: Serializable {

    abstract val url: ShorteningUrl

    data class Photo(override val url: ShorteningUrl) : Media()
}