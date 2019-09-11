package com.droibit.looking2.core.data.repository.account

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.account.service.TwitterAccountService
import com.droibit.looking2.core.data.source.local.twitter.TwitterLocalStore
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.model.account.AuthenticationResult.WillAuthenticateOnPhone
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.model.account.toAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.droibit.looking2.core.model.account.AuthenticationResult.Failure as AuthenticationFailure
import com.droibit.looking2.core.model.account.AuthenticationResult.Success as AuthenticationSuccess

internal class AccountRepositoryImpl @Inject constructor(
    private val twitterService: TwitterAccountService,
    private val localStore: TwitterLocalStore,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : AccountRepository {

    override suspend fun activeAccount(): TwitterAccount? {
        return localStore.activeSession()?.toAccount()
    }

    override suspend fun authenticateTwitter(): Flow<AuthenticationResult> = flow {
        try {
            val requestToken = twitterService.requestTempToken()
            val responseUrl = withContext(dispatcherProvider.main) {
                emit(WillAuthenticateOnPhone)
                twitterService.sendAuthorizationRequest(requestToken)
            }
            val session = twitterService.createNewSession(requestToken, responseUrl)
            localStore.add(session)
            emit(AuthenticationSuccess)
        } catch (e: AuthenticationError) {
            emit(AuthenticationFailure(error = e))
        }

    }
}