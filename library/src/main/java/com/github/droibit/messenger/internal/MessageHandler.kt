package com.github.droibit.messenger.internal

import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.MessageEvent
import kotlinx.coroutines.experimental.CancellationException
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import kotlinx.coroutines.experimental.withTimeout

internal class MessageHandler internal constructor(
        private val expectedPaths: Set<String>,
        private val waitMessageMillis: Long,
        private val dispatcher: Dispatcher) : MessageApi.MessageListener {

    internal class Dispatcher {

        private var messageEvent: MessageEvent? = null

        var callback: ((MessageEvent) -> Unit)? = null
            set(newCallback) {
                field = newCallback
                messageEvent?.let { newCallback?.invoke(it) }
            }

        fun dispatchMessageEvent(messageEvent: MessageEvent) {
            this.messageEvent = messageEvent
            this.callback?.invoke(messageEvent)
        }
    }

    class Factory(private val waitMessageMillis: Long) {

        internal fun create(expectedPaths: Set<String>): MessageHandler {
            return MessageHandler(expectedPaths, waitMessageMillis, Dispatcher())
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path !in expectedPaths) {
            return
        }
        dispatcher.dispatchMessageEvent(messageEvent)
    }

    @Throws(CancellationException::class)
    suspend fun obtain(): MessageEvent {
        return withTimeout(waitMessageMillis) {
            suspendCancellableCoroutine<MessageEvent> { context ->
                dispatcher.callback = { context.resume(it) }
                context.invokeOnCompletion(onCancelling = true) {
                    dispatcher.callback = null
                }
            }
        }
    }
}