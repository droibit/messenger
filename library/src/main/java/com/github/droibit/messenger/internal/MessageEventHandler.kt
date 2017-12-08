package com.github.droibit.messenger.internal

import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageEvent
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import kotlinx.coroutines.experimental.withTimeout
import kotlin.coroutines.experimental.Continuation

internal class MessageEventHandler internal constructor(
        private val expectedPaths: Set<String>,
        private val waitMessageMillis: Long,
        private val dispatcher: Dispatcher) : MessageApi.MessageListener {

    internal class Dispatcher {

        private var messageEvent: MessageEvent? = null

        var continuation: Continuation<MessageEvent>? = null
            set(value) {
                field = value
                messageEvent?.let { value?.resume(it) }
            }

        fun dispatchMessageEvent(messageEvent: MessageEvent) {
            this.messageEvent = messageEvent
            this.continuation?.resume(messageEvent)
        }
    }

    internal class Factory(private val waitMessageMillis: Long) {

        fun create(expectedPaths: Set<String>): MessageEventHandler {
            return MessageEventHandler(expectedPaths, waitMessageMillis, Dispatcher())
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path !in expectedPaths) {
            return
        }
        dispatcher.dispatchMessageEvent(messageEvent)
    }

    suspend fun obtain(): MessageEvent {
        return withTimeout(waitMessageMillis) {
            suspendCancellableCoroutine<MessageEvent> { cont ->
                dispatcher.continuation = cont
                cont.invokeOnCompletion {
                    dispatcher.continuation = null
                }
            }
        }
    }
}