package com.droibit.looking2.ui.common

import android.content.Context
import androidx.annotation.StringRes
import androidx.annotation.UiThread
import kotlin.LazyThreadSafetyMode.NONE

interface ToastConvertible {
    val longDuration: Boolean
    fun message(context: Context): String
}

data class StringResourceToast(
    @StringRes val resId: Int,
    override val longDuration: Boolean = false
) : ToastConvertible {
    override fun message(context: Context): String = context.getString(resId)

    companion object {

        @get:UiThread
        val Network: ToastConvertible by lazy(NONE) {
            StringResourceToast(R.string.error_message_network_disconnected)
        }

        @get:UiThread
        val RateLimited: ToastConvertible by lazy(NONE) {
            StringResourceToast(R.string.error_message_rate_limiting, longDuration = true)
        }

        @get:UiThread
        val UnauthorizedTwitterAccount: ToastConvertible by lazy(NONE) {
            StringResourceToast(
                R.string.error_message_unauthorized_twitter_account,
                longDuration = true
            )
        }
    }
}
