package com.github.droibit.messenger.internal

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageClient.OnMessageReceivedListener
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable

internal interface WearableClient {

  class ClientProvider(private val context: Context) {

    val nodeClient: NodeClient by lazy { Wearable.getNodeClient(context) }

    val messageClient: MessageClient by lazy { Wearable.getMessageClient(context) }

    val capabilityClient: CapabilityClient by lazy { Wearable.getCapabilityClient(context) }
  }

  @Throws(ApiException::class)
  suspend fun getConnectedNodes(): List<Node>

  @Throws(ApiException::class)
  suspend fun getCapability(
    capability: String,
    nodeFilter: Int
  ): CapabilityInfo

  @Throws(ApiException::class)
  suspend fun sendMessage(
    nodeId: String,
    path: String,
    data: ByteArray?
  ): Int

  @Throws(ApiException::class)
  suspend fun addListener(listener: OnMessageReceivedListener)

  @Throws(ApiException::class)
  suspend fun removeListener(listener: OnMessageReceivedListener): Boolean
}