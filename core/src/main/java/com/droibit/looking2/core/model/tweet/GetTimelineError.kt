package com.droibit.looking2.core.model.tweet

// TODO: Consider adding auth error & limited api error.
sealed class GetTimelineError(message: String? = null) : Exception(message) {
    class Network : GetTimelineError()
    class UnExpected : GetTimelineError()
}