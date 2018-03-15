package com.github.droibit.messenger

import com.google.android.gms.common.api.Status

class MessengerException(val error: Status) : Exception() {

  override val message get() = error.toString()
}