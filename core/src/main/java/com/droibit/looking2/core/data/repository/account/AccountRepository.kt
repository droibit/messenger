package com.droibit.looking2.core.data.repository.account

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterSource
import com.droibit.looking2.core.data.source.remote.twitter.account.RemoteTwitterAccountSource
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.model.account.AuthenticationResult.WillAuthenticateOnPhone
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.model.account.toAccount
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import com.droibit.looking2.core.model.account.AuthenticationResult.Failure as AuthenticationFailure
import com.droibit.looking2.core.model.account.AuthenticationResult.Success as AuthenticationSuccess

@Singleton
class AccountRepository(
    private val remoteSource: RemoteTwitterAccountSource,
    private val localSource: LocalTwitterSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val twitterAccountsChannel: ConflatedBroadcastChannel<List<TwitterAccount>>,
    private val analytics: AnalyticsHelper
) {
    @Inject
    constructor(
        remoteSource: RemoteTwitterAccountSource,
        localSource: LocalTwitterSource,
        dispatcherProvider: CoroutinesDispatcherProvider,
        analytics: AnalyticsHelper
    ) : this(
        remoteSource,
        localSource,
        dispatcherProvider,
        ConflatedBroadcastChannel<List<TwitterAccount>>(),
        analytics
    )

    suspend fun initialize(): Unit = withContext(dispatcherProvider.io) {
        localSource.sessions.forEach { remoteSource.ensureApiClient(session = it) }
        dispatchTwitterAccountsUpdated()
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    fun twitterAccounts(): Flow<List<TwitterAccount>> {
        return twitterAccountsChannel.asFlow()
    }

    suspend fun updateActiveTwitterAccount(account: TwitterAccount) {
        withContext(dispatcherProvider.io) {
            val session = localSource.getSessionBy(account.id)
            checkNotNull(session) { "Account dose not exist: $account" }

            if (session != localSource.activeSession) {
                localSource.setActiveSession(session)
                dispatchTwitterAccountsUpdated()
            }
        }
    }

    @Throws(AuthenticationError::class)
    suspend fun signInTwitter(): Flow<AuthenticationResult> = flow {
        try {
            val requestToken = remoteSource.requestTempToken()
            emit(WillAuthenticateOnPhone)
            val responseUrl = remoteSource.sendAuthorizationRequest(requestToken)
            val newSession = remoteSource.createNewSession(requestToken, responseUrl)
            localSource.add(newSession)
            remoteSource.ensureApiClient(newSession)
            analytics.setNumOfGetTweets(localSource.sessions.size)
            dispatchTwitterAccountsUpdated()

            emit(AuthenticationSuccess)
        } catch (e: AuthenticationError) {
            emit(AuthenticationFailure(error = e))
        }
    }.flowOn(dispatcherProvider.io)

    suspend fun signOutTwitter(account: TwitterAccount) {
        withContext(dispatcherProvider.io) {
            localSource.remove(account.id)
            analytics.setNumOfGetTweets(localSource.sessions.size)

            if (localSource.activeSession == null) {
                localSource.sessions.firstOrNull()?.let {
                    localSource.setActiveSession(it)
                }
            }
            dispatchTwitterAccountsUpdated()
        }
    }

    private fun dispatchTwitterAccountsUpdated() {
        val activeAccount = localSource.activeSession
        val accounts = localSource.sessions.map {
            it.toAccount(active = it.userId == activeAccount?.userId)
        }
        twitterAccountsChannel.offer(accounts)
    }
}