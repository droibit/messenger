package com.droibit.looking2.core.data.source.remote.twitter.api.oauth

import java.lang.Exception

internal class PhoneAuthenticationException(
    val errorCode: Int
) : Exception("remote authentication request failed: $errorCode")
