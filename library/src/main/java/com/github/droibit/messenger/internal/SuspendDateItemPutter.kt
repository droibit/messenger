package com.github.droibit.messenger.internal

import com.google.android.gms.wearable.DataApi.DataItemResult
import com.google.android.gms.wearable.PutDataRequest


internal interface SuspendDateItemPutter {

    suspend fun putDataItem(request: PutDataRequest): DataItemResult
}