package com.github.droibit.messenger.internal

import com.google.android.gms.common.api.Status
import com.google.android.gms.wearable.CapabilityApi
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageApi.MessageListener
import com.google.android.gms.wearable.NodeApi

internal interface SuspendMessageSender {

  suspend fun getConnectedNodes(): NodeApi.GetConnectedNodesResult

  suspend fun getCapability(
    capability: String,
    nodeFilter: Int
  ): CapabilityApi.GetCapabilityResult

  suspend fun sendMessage(
    nodeId: String,
    path: String,
    data: ByteArray?
  ): MessageApi.SendMessageResult

  suspend fun addListener(listener: MessageListener): Status

  suspend fun removeListener(listener: MessageListener): Status
}