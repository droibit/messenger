package com.droibit.looking2.account.ui

import android.app.Activity
import com.droibit.looking2.core.ui.Activities.Account.EXTRA_NEED_TWITTER_SIGN_IN
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Named

@InstallIn(ActivityComponent::class)
@Module
object AccountHostModule {

    @Named("needTwitterSignIn")
    @Provides
    fun provideNeedTwitterSignIn(activity: Activity): Boolean {
        val intent = requireNotNull(activity.intent)
        return intent.getBooleanExtra(EXTRA_NEED_TWITTER_SIGN_IN, false)
    }
}
