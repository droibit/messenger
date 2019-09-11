package com.droibit.looking2.core.data.repository.account

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.account.service.TwitterAccountService
import com.droibit.looking2.core.model.account.AuthenticationError
import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.model.account.AuthenticationResult.WillAuthenticateOnPhone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.droibit.looking2.core.model.account.AuthenticationResult.Failure as AuthenticationFailure
import com.droibit.looking2.core.model.account.AuthenticationResult.Success as AuthenticationSuccess

internal class AccountRepositoryImpl @Inject constructor(
    private val twitterService: TwitterAccountService,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : AccountRepository {

    override suspend fun authenticateTwitter(): Flow<AuthenticationResult> = flow {
        try {
            val requestToken = twitterService.requestTempToken()

            val responseUrl = withContext(dispatcherProvider.main) {
                emit(WillAuthenticateOnPhone)
                twitterService.sendAuthorizationRequest(requestToken)
            }

            val session = twitterService.createNewSession(requestToken, responseUrl)
            // TODO: Store new session
            emit(AuthenticationSuccess)
        } catch (e: AuthenticationError) {
            emit(AuthenticationFailure(error = e))
        }

    }
}