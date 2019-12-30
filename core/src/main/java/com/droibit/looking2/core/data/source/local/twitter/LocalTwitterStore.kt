package com.droibit.looking2.core.data.source.local.twitter

import com.droibit.looking2.core.data.source.api.twitter.AppTwitterApiClient
import com.twitter.sdk.android.core.SessionManager
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Singleton
class LocalTwitterStore @Inject constructor(
    private val twitterCore: TwitterCore,
    private val sessionManager: SessionManager<TwitterSession>,
    private val apiClientFactory: AppTwitterApiClient.Factory
) {

    suspend fun activeSession(): TwitterSession? = suspendCoroutine { cont ->
        cont.resume(sessionManager.activeSession)
    }

    @Throws(IllegalArgumentException::class)
    suspend fun updateActiveSession(sessionId: Long): Boolean = suspendCoroutine { cont ->
        val session = sessionManager.getSession(sessionId)
        if (session == null) {
            cont.resumeWithException(IllegalArgumentException("Invalid sessionId($sessionId)."))
            return@suspendCoroutine
        }

        if (sessionId == sessionManager.activeSession?.userId) {
            cont.resume(false)
        } else {
            sessionManager.activeSession = session
            cont.resume(true)
        }
    }

    suspend fun sessions(): List<TwitterSession> = suspendCoroutine { cont ->
        cont.resume(sessionManager.sessionMap.map { (_, session) -> session })
    }

    suspend fun add(session: TwitterSession) =
        suspendCoroutine<Unit> { cont ->
            Timber.d("Add: $session")
            sessionManager.setSession(session.id, session)
            twitterCore.addApiClient(session, apiClientFactory.get(session))
            cont.resume(Unit)
        }

    suspend fun remove(sessionId: Long) = suspendCoroutine<Unit> { cont ->
        sessionManager.clearSession(sessionId)
        cont.resume(Unit)
    }
}