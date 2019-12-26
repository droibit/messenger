package com.droibit.looking2.timeline.ui.content

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy.KEEP
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.droibit.looking2.core.data.repository.tweet.TweetRepository
import com.droibit.looking2.core.model.tweet.TwitterError
import com.droibit.looking2.core.model.tweet.retryIfNeeded
import com.droibit.looking2.coreComponent
import com.droibit.looking2.timeline.ui.content.TweetActionCall.Companion.KEY_TWEET_ID
import timber.log.Timber
import javax.inject.Inject

class TweetActionCall @Inject constructor(
    private val workManager: WorkManager
) {
    fun enqueueRetweetWork(tweetId: Long) {
        val work = OneTimeWorkRequestBuilder<RetweetActionWorker>()
            .setInputData(workDataOf(KEY_TWEET_ID to tweetId))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        val workName = WORK_NAME_PREFIX_RETWEET + "$tweetId"
        workManager.enqueueUniqueWork(workName, KEEP, work)
    }

    fun enqueueLikesWork(tweetId: Long) {
        val work = OneTimeWorkRequestBuilder<LikeTweetActionWorker>()
            .setInputData(workDataOf(KEY_TWEET_ID to tweetId))
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        val workName = WORK_NAME_PREFIX_LIKES + "$tweetId"
        workManager.enqueueUniqueWork(workName, KEEP, work)
    }

    companion object {
        const val KEY_TWEET_ID = "KEY_TWEET_ID"
        const val WORK_NAME_PREFIX_RETWEET = "timeline:retweet/"
        const val WORK_NAME_PREFIX_LIKES = "timeline:like_tweet/"
    }
}

class RetweetActionWorker(
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
        val tweetId = inputData.getLong(KEY_TWEET_ID, Long.MIN_VALUE).also {
            Timber.d("Do work-$runAttemptCount: retweet:$it")
        }
        check(tweetId != Long.MIN_VALUE)

        return try {
            tweetRepository.retweet(tweetId)
            Result.success()
        } catch (error: TwitterError) {
            retryIfNeeded(cause = error)
        }
    }
}

class LikeTweetActionWorker(
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
        val tweetId = inputData.getLong(KEY_TWEET_ID, Long.MIN_VALUE).also {
            Timber.d("Do work-$runAttemptCount: like tweet:$it")
        }
        check(tweetId != Long.MIN_VALUE)

        return try {
            tweetRepository.likeTweet(tweetId)
            Result.success()
        } catch (error: TwitterError) {
            retryIfNeeded(cause = error)
        }
    }
}