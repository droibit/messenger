package com.droibit.looking2.core.data.repository.account

import com.droibit.looking2.core.data.CoroutinesDispatcherProvider
import com.droibit.looking2.core.data.repository.account.service.TwitterAccountService
import com.droibit.looking2.core.model.account.AuthenticationResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val twitterService: TwitterAccountService,
    private val dispatcherProvider: CoroutinesDispatcherProvider
) : AccountRepository {

    override suspend fun authenticateTwitter(): Flow<AuthenticationResult> = flow {

    }
}