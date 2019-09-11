package com.droibit.looking2.core.data.repository.account

import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.model.account.TwitterAccount
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    suspend fun activeAccount(): TwitterAccount?

    suspend fun authenticateTwitter(): Flow<AuthenticationResult>
}