package com.droibit.looking2.core.data.repository.account

import com.droibit.looking2.core.model.account.AuthenticationResult
import com.droibit.looking2.core.model.account.TwitterAccount
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow

interface AccountRepository {

    val twitterAccounts: BroadcastChannel<List<TwitterAccount>>

    suspend fun activeAccount(): TwitterAccount?

    suspend fun authenticateTwitter(): Flow<AuthenticationResult>
}