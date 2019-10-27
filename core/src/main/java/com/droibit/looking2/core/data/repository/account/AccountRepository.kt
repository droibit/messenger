package com.droibit.looking2.core.data.repository.account

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.account.service.TwitterAccountService
import com.droibit.looking2.core.data.source.local.twitter.LocalTwitterStore
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.model.account.AuthenticationResult.WillAuthenticateOnPhone
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.model.account.toAccount
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton
import com.droibit.looking2.core.model.account.AuthenticationResult.Failure as AuthenticationFailure
import com.droibit.looking2.core.model.account.AuthenticationResult.Success as AuthenticationSuccess

@Singleton
class AccountRepository(
    private val twitterService: TwitterAccountService,
    private val localStore: LocalTwitterStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val twitterAccountsChannel: BroadcastChannel<List<TwitterAccount>>
) {

    @Inject
    constructor(
        twitterService: TwitterAccountService,
        localStore: LocalTwitterStore,
        dispatcherProvider: CoroutinesDispatcherProvider
    ) : this(
        twitterService,
        localStore,
        dispatcherProvider,
        ConflatedBroadcastChannel<List<TwitterAccount>>()
    )

    val twitterAccounts: BroadcastChannel<List<TwitterAccount>>
        get() = twitterAccountsChannel

    suspend fun initialize() {
        localStore.sessions().forEach { twitterService.ensureApiClient(session = it) }
        dispatchTwitterAccountsUpdated()
    }

    suspend fun activeTwitterAccount(): TwitterAccount? {
        return localStore.activeSession()?.toAccount()
    }

    @Throws(AuthenticationError::class)
    suspend fun authenticateTwitter(): Flow<AuthenticationResult> = flow {
        try {
            val requestToken = twitterService.requestTempToken()
            emit(WillAuthenticateOnPhone)
            val responseUrl = twitterService.sendAuthorizationRequest(requestToken)
            twitterService.createNewSession(requestToken, responseUrl).also {
                localStore.add(session = it)
                dispatchTwitterAccountsUpdated()
            }
            emit(AuthenticationSuccess)
        } catch (e: AuthenticationError) {
            emit(AuthenticationFailure(error = e))
        }
    }.flowOn(dispatcherProvider.io)

    private suspend fun dispatchTwitterAccountsUpdated() {
        val accounts = localStore.sessions().map { it.toAccount() }
        twitterAccountsChannel.offer(accounts)
    }
}