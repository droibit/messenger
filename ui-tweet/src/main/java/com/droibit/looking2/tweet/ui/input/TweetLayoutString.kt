package com.droibit.looking2.tweet.ui.input

import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import javax.inject.Named

@FragmentScoped
data class TweetLayoutString @Inject constructor(
    @Named("title") val title: String,
    @Named("replyUser") val replyUser: String,
    @Named("tweetTextHint") val tweetTextHint: String
)
