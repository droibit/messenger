package com.droibit.looking2.tweet.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.droibit.looking2.tweet.R
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class TweetActivity : FragmentActivity(R.layout.activity_tweet), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = androidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
    }
}