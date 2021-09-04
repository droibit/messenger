package com.droibit.looking2.ui.common

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.wear.activity.ConfirmationActivity
import androidx.wear.activity.ConfirmationActivity.EXTRA_ANIMATION_TYPE
import androidx.wear.activity.ConfirmationActivity.EXTRA_MESSAGE
import androidx.wear.activity.ConfirmationActivity.FAILURE_ANIMATION
import androidx.wear.activity.ConfirmationActivity.OPEN_ON_PHONE_ANIMATION
import androidx.wear.activity.ConfirmationActivity.SUCCESS_ANIMATION

object Activities {

    @Suppress("FunctionName")
    object Confirmation {

        fun SuccessIntent(context: Context, @StringRes messageResId: Int?): Intent {
            return Intent(context, ConfirmationActivity::class.java)
                .putExtra(EXTRA_ANIMATION_TYPE, SUCCESS_ANIMATION)
                .apply {
                    if (messageResId != null) {
                        this.putExtra(EXTRA_MESSAGE, context.getString(messageResId))
                    }
                }
        }

        fun FailureIntent(context: Context, @StringRes messageResId: Int): Intent {
            return Intent(context, ConfirmationActivity::class.java)
                .putExtra(EXTRA_ANIMATION_TYPE, FAILURE_ANIMATION)
                .putExtra(EXTRA_MESSAGE, context.getString(messageResId))
        }

        fun OpenOnPhoneIntent(context: Context, @StringRes messageResId: Int? = null): Intent {
            return Intent(context, ConfirmationActivity::class.java)
                .putExtra(EXTRA_ANIMATION_TYPE, OPEN_ON_PHONE_ANIMATION)
                .apply {
                    if (messageResId != null) {
                        putExtra(EXTRA_MESSAGE, context.getString(messageResId))
                    }
                }
        }
    }

    fun createRestartIntent(context: Context): Intent {
        val packageName = context.packageName
        return requireNotNull(
            context.packageManager.getLaunchIntentForPackage(packageName)
        )
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}
