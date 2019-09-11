package com.droibit.looking2.core.data.source.local.twitter

import com.twitter.sdk.android.core.TwitterSession

interface TwitterLocalStore {

    suspend fun activeSession(): TwitterSession?

    suspend fun sessions(): List<TwitterSession>

    suspend fun add(session: TwitterSession)

    suspend fun remove(sessionId: Long)
}