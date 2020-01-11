package com.droibit.looking2.core.data.repository.account

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.account.service.TwitterAccountService
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterStore
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.model.account.AuthenticationResult.WillAuthenticateOnPhone
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.model.account.toAccount
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
    private val twitterService: TwitterAccountService,
    private val localStore: LocalTwitterStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val twitterAccountsChannel: ConflatedBroadcastChannel<List<TwitterAccount>>,
) {
    @Inject
    constructor(
        twitterService: TwitterAccountService,
        localStore: LocalTwitterStore,
        dispatcherProvider: CoroutinesDispatcherProvider,
    ) : this(
        twitterService,
        localStore,
        dispatcherProvider,
        ConflatedBroadcastChannel<List<TwitterAccount>>(),
    )

    suspend fun initialize(): Unit = withContext(dispatcherProvider.io) {
        localStore.sessions.forEach { twitterService.ensureApiClient(session = it) }
        dispatchTwitterAccountsUpdated()
    }

    @Suppress("EXPERIMENTAL_API_USAGE")
    fun twitterAccounts(): Flow<List<TwitterAccount>> {
        return twitterAccountsChannel.asFlow()
    }

    suspend fun updateActiveTwitterAccount(account: TwitterAccount) {
        withContext(dispatcherProvider.io) {
            val session = localStore.getSession(account.id)
            checkNotNull(session) { "Account dose not exist: $account" }

            if (session != localStore.activeSession) {
                localStore.setActiveSession(session)
                dispatchTwitterAccountsUpdated()
            }
        }
    }

    @Throws(AuthenticationError::class)
    suspend fun signInTwitter(): Flow<AuthenticationResult> = flow {
        try {
            val requestToken = twitterService.requestTempToken()
            emit(WillAuthenticateOnPhone)
            val responseUrl = twitterService.sendAuthorizationRequest(requestToken)
            twitterService.createNewSession(requestToken, responseUrl).also {
                localStore.add(session = it)
                analytics.setNumOfGetTweets(localStore.sessions.size)
                dispatchTwitterAccountsUpdated()
            }
            emit(AuthenticationSuccess)
        } catch (e: AuthenticationError) {
            emit(AuthenticationFailure(error = e))
        }
    }.flowOn(dispatcherProvider.io)

    suspend fun signOutTwitter(account: TwitterAccount) {
        withContext(dispatcherProvider.io) {
            localStore.remove(account.id)
            analytics.setNumOfGetTweets(localStore.sessions.size)

            if (localStore.activeSession == null) {
                localStore.sessions.firstOrNull()?.let {
                    localStore.setActiveSession(it)
                }
            }
            dispatchTwitterAccountsUpdated()
        }
    }

    private fun dispatchTwitterAccountsUpdated() {
        val activeAccount = localStore.activeSession
        val accounts = localStore.sessions.map {
            it.toAccount(active = it.userId == activeAccount?.userId)
        }
        twitterAccountsChannel.offer(accounts)
    }
}