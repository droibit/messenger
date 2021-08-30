package com.droibit.looking2.core.data.repository.account

import androidx.annotation.VisibleForTesting
import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterSource
import com.droibit.looking2.core.data.source.remote.twitter.account.RemoteTwitterAccountSource
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.model.account.AuthenticationResult.Failure as AuthenticationFailure
import com.droibit.looking2.core.model.account.AuthenticationResult.Success as AuthenticationSuccess
import com.droibit.looking2.core.model.account.AuthenticationResult.WillAuthenticateOnPhone
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.model.account.toAccount
import com.droibit.looking2.core.util.analytics.AnalyticsHelper
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

@Singleton
class AccountRepository(
    private val remoteSource: RemoteTwitterAccountSource,
    private val localSource: LocalTwitterSource,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val twitterAccountsSink: MutableStateFlow<List<TwitterAccount>>,
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
        MutableStateFlow<List<TwitterAccount>>(emptyList()),
        analytics
    )

    suspend fun initialize(): Unit = withContext(dispatcherProvider.io) {
        localSource.sessions.forEach { remoteSource.ensureApiClient(session = it) }
        dispatchTwitterAccountsUpdated()
    }

    fun twitterAccounts(): Flow<List<TwitterAccount>> {
        return twitterAccountsSink
    }

    suspend fun updateActiveTwitterAccount(accountId: Long) {
        withContext(dispatcherProvider.io) {
            val session = localSource.getSessionBy(accountId)
            checkNotNull(session) { "Account dose not exist: $accountId" }

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

    suspend fun signOutTwitter(accountId: Long) {
        withContext(dispatcherProvider.io) {
            localSource.remove(accountId)
            analytics.setNumOfGetTweets(localSource.sessions.size)

            if (localSource.activeSession == null) {
                localSource.sessions.firstOrNull()?.let {
                    localSource.setActiveSession(it)
                }
            }
            dispatchTwitterAccountsUpdated()
        }
    }

    @VisibleForTesting
    internal fun dispatchTwitterAccountsUpdated() {
        val activeAccount = localSource.activeSession
        if (activeAccount == null) {
            twitterAccountsSink.value = emptyList()
            return
        }

        val accounts = localSource.sessions.map {
            it.toAccount(active = it.userId == activeAccount.userId)
        }
        twitterAccountsSink.value = accounts
    }
}
