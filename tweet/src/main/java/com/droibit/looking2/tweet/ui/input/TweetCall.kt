package com.droibit.looking2.tweet.ui.input

import com.droibit.looking2.core.data.repository.tweet.TweetRepository
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.tweet.R
import com.droibit.looking2.tweet.ui.input.TweetResult.FailureType
import com.droibit.looking2.ui.Activities.Tweet.ReplyTweet

sealed class TweetCall {

    @Throws(FailureType::class)
    abstract suspend fun call(text: String): SuccessfulMessage

    class Tweet(private val tweetRepository: TweetRepository) : TweetCall() {
        override suspend fun call(text: String): SuccessfulMessage {
            try {
                tweetRepository.tweet(text)
                return SuccessfulMessage(R.string.tweet_tweet_successful)
            } catch (e: TwitterError) {
                throw when (e) {
                    is TwitterError.Network -> FailureType.Network
                    is TwitterError.UnExpected -> FailureType.UnExpected(R.string.tweet_tweet_failed)
                    is TwitterError.Limited -> TODO("Not implemented.")
                    is TwitterError.Unauthorized -> TODO("Not implemented.")
                }
            }
        }
    }

    class Reply(
        private val tweetRepository: TweetRepository,
        private val replyTweetId: Long
    ) : TweetCall() {
        override suspend fun call(text: String): SuccessfulMessage {
            try {
                tweetRepository.tweet(text, replyTweetId)
                return SuccessfulMessage(R.string.tweet_reply_failed)
            } catch (e: TwitterError) {
                throw when (e) {
                    is TwitterError.Network -> FailureType.Network
                    is TwitterError.UnExpected -> FailureType.UnExpected(R.string.tweet_reply_failed)
                    is TwitterError.Limited -> TODO("Not implemented.")
                    is TwitterError.Unauthorized -> TODO("Not implemented.")
                }
            }
        }
    }

    companion object {

        operator fun invoke(
            repository: TweetRepository,
            replyTweet: ReplyTweet?
        ): TweetCall {
            return if (replyTweet == null) {
                Tweet(repository)
            } else {
                Reply(repository, replyTweet.id)
            }
        }
    }
}

