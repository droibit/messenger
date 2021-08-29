package com.droibit.looking2.account.ui

import com.droibit.looking2.core.ui.Activities.Account.EXTRA_NEED_TWITTER_SIGN_IN
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Named

@InstallIn(ActivityComponent::class)
@Module
object AccountHostModule {

    @Named("needTwitterSignIn")
    @Provides
    fun provideNeedTwitterSignIn(activity: AccountHostActivity): Boolean {
        val intent = requireNotNull(activity.intent)
        return intent.getBooleanExtra(EXTRA_NEED_TWITTER_SIGN_IN, false)
    }

    @ActivityScoped
    @Provides
    fun provideContentPadding(activity: AccountHostActivity): ShapeAwareContentPadding {
        return ShapeAwareContentPadding(activity)
    }
}
