package com.droibit.looking2.core.model.tweet

// TODO: Consider adding auth error & limited api error.
sealed class TweetActionError(message: String? = null) : Exception(message) {
    class Network : TweetActionError()
    class UnExpected : TweetActionError()
}