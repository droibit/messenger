
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
package com.droibit.looking2.ui

import android.content.Intent
import com.droibit.looking2.BuildConfig

/**
 * Create an Intent with [Intent.ACTION_VIEW] to an [AddressableActivity].
 */
fun intentTo(addressableActivity: AddressableActivity): Intent {
    return Intent().setClassName(
        BuildConfig.APPLICATION_ID,
        addressableActivity.className)
}

/**
 * An [android.app.Activity] that can be addressed by an intent.
 */
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
object Activities {

    /**
     * AccountActivity
     */
    object Account : AddressableActivity {
        override val className = "${BuildConfig.PACKAGE_NAME}.account.ui.AccountActivity"

        const val EXTRA_LOGIN_TWITTER = "EXTRA_SHOT_ID"

        fun createIntent(loginTwitter: Boolean): Intent {
            return intentTo(Account).apply {
                putExtra(EXTRA_LOGIN_TWITTER, loginTwitter)
            }
        }
    }
}