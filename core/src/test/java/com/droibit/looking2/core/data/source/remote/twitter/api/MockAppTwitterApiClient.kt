package com.droibit.looking2.core.data.source.remote.twitter.api

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock

fun mockAppTwitterApiClient(): AppTwitterApiClient {
    return mock {
        on { this.statusesService } doReturn mock()
        on { this.userListService } doReturn mock()
        on { this.favoriteService } doReturn mock()
    }
}