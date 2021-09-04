package com.droibit.looking2.tweet.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Optional
import javax.inject.Inject

@HiltViewModel
class TweetHostViewModel @Inject constructor(
    val layoutString: TweetLayoutString,
    val replyTweet: Optional<ReplyTweet>
) : ViewModel()
