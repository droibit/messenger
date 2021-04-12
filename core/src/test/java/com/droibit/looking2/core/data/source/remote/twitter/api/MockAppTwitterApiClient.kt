package com.droibit.looking2.core.data.source.remote.twitter.api

import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

fun mockAppTwitterApiClient(): AppTwitterApiClient {
    return mock {
        on { this.statusesService } doReturn mock()
        on { this.userListService } doReturn mock()
        on { this.favoriteService } doReturn mock()
    }
}
