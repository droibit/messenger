package com.droibit.looking2.core.data.source.local.twitter

import androidx.annotation.Size
import androidx.annotation.WorkerThread
import com.droibit.looking2.core.data.source.api.twitter.AppTwitterApiClient
import com.twitter.sdk.android.core.SessionManager
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterSession
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalTwitterStore @Inject constructor(
    private val twitterCore: TwitterCore,
    private val sessionManager: SessionManager<TwitterSession>,
    private val apiClientFactory: AppTwitterApiClient.Factory
) {
    @get:WorkerThread
    val activeSession: TwitterSession?
        get() = sessionManager.activeSession

    @get:[WorkerThread Size(min = 0)]
    val sessions: List<TwitterSession>
        get() = sessionManager.sessionMap.map { (_, session) -> session }

    @WorkerThread
    fun getSession(id: Long): TwitterSession? {
        return sessionManager.sessionMap[id]
    }

    @WorkerThread
    fun setActiveSession(session: TwitterSession) {
        sessionManager.activeSession = session
    }

    @WorkerThread
    fun add(session: TwitterSession) {
        Timber.d("Add: $session")
        sessionManager.setSession(session.id, session)
        twitterCore.addApiClient(session, apiClientFactory.get(session))
    }

    @WorkerThread
    fun remove(sessionId: Long) {
        sessionManager.clearSession(sessionId)
    }
}