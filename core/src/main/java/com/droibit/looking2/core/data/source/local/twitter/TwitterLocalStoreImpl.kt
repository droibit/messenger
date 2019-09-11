package com.droibit.looking2.core.data.source.local.twitter

import com.droibit.looking2.core.data.source.api.twitter.LookingTwitterApiClient
import com.twitter.sdk.android.core.SessionManager
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

internal class TwitterLocalStoreImpl @Inject constructor(
    private val twitterCore: TwitterCore,
    private val sessionManager: SessionManager<TwitterSession>,
    private val apiClientFactory: LookingTwitterApiClient.Factory
) : TwitterLocalStore {

    override suspend fun activeSession(): TwitterSession? = suspendCoroutine { cont ->
        cont.resume(sessionManager.activeSession)
    }

    override suspend fun sessions(): List<TwitterSession> = suspendCoroutine { cont ->
        cont.resume(sessionManager.sessionMap.map { (_, session) -> session })
    }

    override suspend fun add(session: TwitterSession) =
        suspendCoroutine<Unit> { cont ->
            Timber.d("Add: $session")
            sessionManager.setSession(session.id, session)
            twitterCore.addApiClient(session, apiClientFactory.create(session))
            cont.resume(Unit)
        }

    override suspend fun remove(sessionId: Long) = suspendCoroutine<Unit> { cont ->
        sessionManager.clearSession(sessionId)
        cont.resume(Unit)
    }
}