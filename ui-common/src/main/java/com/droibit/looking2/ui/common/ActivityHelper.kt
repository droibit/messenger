/*
 * Copyright 2018 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:JvmName("ActivityHelper")

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
import com.droibit.looking2.core.model.tweet.User
import java.io.Serializable

/**
 * Create an Intent with [Intent.ACTION_VIEW] to an [AddressableActivity].
 */
@Deprecated("Discontinue Dynamic Feature Module.")
fun intentTo(addressableActivity: AddressableActivity): Intent {
    return Intent().setClassName(
        "com.droibit.looking2.debug",
        addressableActivity.className
    )
}

/**
 * An [android.app.Activity] that can be addressed by an intent.
 */
@Deprecated("Discontinue Dynamic Feature Module.")
interface AddressableActivity {
    /**
     * The activity class name.
     */
    val className: String
}

/**
 * All addressable activities.
 *
 * Can contain intent extra names or functions associated with the activity creation.
 */
@Deprecated("Discontinue Dynamic Feature Module.")
object Activities {

    @Deprecated("Discontinue Dynamic Feature Module.")
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

    /**
     * AccountActivity
     */
    @Deprecated("Discontinue Dynamic Feature Module.")
    object Account : AddressableActivity {
        override val className = "com.droibit.looking2.account.ui.AccountHostActivity"

        const val EXTRA_NEED_TWITTER_SIGN_IN = "EXTRA_NEED_TWITTER_SIGN_IN"

        fun createIntent(needTwitterSignIn: Boolean): Intent {
            return intentTo(Account)
                .putExtra(EXTRA_NEED_TWITTER_SIGN_IN, needTwitterSignIn)
        }

        fun createRestartIntent(): Intent {
            return intentTo(Account)
                .putExtra(EXTRA_NEED_TWITTER_SIGN_IN, true)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * HomeActivity
     */
    @Deprecated("Discontinue Dynamic Feature Module.")
    object Home : AddressableActivity {
        override val className = "com.droibit.looking2.home.ui.HomeActivity"

        fun createIntent(): Intent = intentTo(Home)
    }

    /**
     * TimelineActivity
     */
    @Deprecated("Discontinue Dynamic Feature Module.")
    object Timeline : AddressableActivity {
        override val className = "com.droibit.looking2.timeline.ui.TimelineHostActivity"

        const val EXTRA_TIMELINE_SOURCE = "EXTRA_TIMELINE_SOURCE"

        const val TIMELINE_SOURCE_HOME = 0
        const val TIMELINE_SOURCE_MENTIONS = 1
        const val TIMELINE_SOURCE_LISTS = 2

        fun createHomeIntent(): Intent {
            return intentTo(Timeline)
                .putExtra(EXTRA_TIMELINE_SOURCE, TIMELINE_SOURCE_HOME)
        }

        fun createMentionsIntent(): Intent {
            return intentTo(Timeline)
                .putExtra(EXTRA_TIMELINE_SOURCE, TIMELINE_SOURCE_MENTIONS)
        }

        fun createListsIntent(): Intent {
            return intentTo(Timeline)
                .putExtra(EXTRA_TIMELINE_SOURCE, TIMELINE_SOURCE_LISTS)
        }
    }

    /**
     * SettingsActivity
     */
    @Deprecated("Discontinue Dynamic Feature Module.")
    object Settings : AddressableActivity {
        override val className = "com.droibit.looking2.settings.ui.SettingsHostActivity"

        fun createIntent(): Intent {
            return intentTo(Settings)
        }
    }

    /**
     * TweetActivity
     */
    @Deprecated("Discontinue Dynamic Feature Module.")
    object Tweet : AddressableActivity {
        const val EXTRA_REPLY_TWEET = "EXTRA_REPLY_TWEET"

        override val className: String = "com.droibit.looking2.tweet.ui.TweetHostActivity"

        fun createIntent(replyTweet: ReplyTweet? = null): Intent {
            return intentTo(Tweet).putExtra(EXTRA_REPLY_TWEET, replyTweet)
        }

        data class ReplyTweet(
            val id: Long,
            val user: User
        ) : Serializable
    }
}
