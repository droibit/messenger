package com.droibit.looking2.tweet.ui.input

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.droibit.looking2.core.data.repository.tweet.TweetRepository
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.retryIfNeeded
import com.droibit.looking2.coreComponent
import com.droibit.looking2.tweet.ui.input.TweetCall.Companion.KEY_REPLY_TWEET_ID
import com.droibit.looking2.tweet.ui.input.TweetCall.Companion.KEY_TWEET
import com.droibit.looking2.ui.Activities.Tweet.ReplyTweet
import timber.log.Timber

sealed class TweetCall(private val workManager: WorkManager) {

    abstract fun buildWorkRequest(text: String): WorkRequest

    fun enqueue(text: String) {
        val work = buildWorkRequest(text)
        workManager.enqueue(work)
    }

    class Tweet(workManager: WorkManager) : TweetCall(workManager) {
        override fun buildWorkRequest(text: String): WorkRequest {
            return OneTimeWorkRequestBuilder<TweetWorker>()
                .setInputData(workDataOf(KEY_TWEET to text))
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        }
    }

    class Reply(
        workManager: WorkManager,
        val replyTweetId: Long
    ) : TweetCall(workManager) {
        override fun buildWorkRequest(text: String): WorkRequest {
            return OneTimeWorkRequestBuilder<ReplyWorker>()
                .setInputData(
                    workDataOf(
                        KEY_TWEET to text,
                        KEY_REPLY_TWEET_ID to replyTweetId
                    )
                )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        }
    }

    companion object {
        const val KEY_TWEET = "KEY_TWEET"
        const val KEY_REPLY_TWEET_ID = "KEY_REPLY_TWEET_ID"

        operator fun invoke(workManager: WorkManager, replyTweet: ReplyTweet?): TweetCall {
            return if (replyTweet == null) {
                Tweet(workManager)
            } else {
                Reply(workManager, replyTweet.id)
            }
        }
    }
}

class TweetWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val tweetRepository: TweetRepository
) : CoroutineWorker(context, workerParams) {

    @Suppress("unused")
    constructor(context: Context, workerParams: WorkerParameters) : this(
        context,
        workerParams,
        context.coreComponent().tweetRepository
    )

    override suspend fun doWork(): Result {
        val text = requireNotNull(inputData.getString(KEY_TWEET))
        Timber.d("Do work-$runAttemptCount: tweet:$text")

        return try {
            tweetRepository.tweet(text)
            Result.success()
        } catch (error: TwitterError) {
            retryIfNeeded(cause = error)
        }
    }
}

class ReplyWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val tweetRepository: TweetRepository
) : CoroutineWorker(context, workerParams) {
    @Suppress("unused")
    constructor(context: Context, workerParams: WorkerParameters) : this(
        context,
        workerParams,
        context.coreComponent().tweetRepository
    )

    override suspend fun doWork(): Result {
        val text = requireNotNull(inputData.getString(KEY_TWEET))
        val replyTweetId = inputData.getLong(KEY_REPLY_TWEET_ID, Long.MIN_VALUE)
            .takeIf { it != Long.MIN_VALUE } ?: error("Missing reply tweet id")
        Timber.d("Do work-$runAttemptCount: reply:$text, to: $replyTweetId")

        return try {
            tweetRepository.tweet(text, replyTweetId)
            Result.success()
        } catch (error: TwitterError) {
            retryIfNeeded(cause = error)
        }
    }
}