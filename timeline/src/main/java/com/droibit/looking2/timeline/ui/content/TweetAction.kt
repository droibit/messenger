package com.droibit.looking2.timeline.ui.content

import android.content.Context
import androidx.annotation.IdRes
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.droibit.looking2.core.data.repository.tweet.TweetRepository
import com.droibit.looking2.core.model.tweet.RetweetError
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.coreComponent
import com.droibit.looking2.timeline.R

data class TweetAction(val target: Tweet, val items: List<Item>) {

    enum class Item(@IdRes val id: Int) {
        REPLY(R.id.tweet_action_reply),
        RETWEET(R.id.tweet_action_retweet),
        LIKES(R.id.tweet_action_likes),
        PHOTO(R.id.tweet_action_show_photo),
        ADD_TO_POCKET(R.id.tweet_action_add_pocket);

        companion object {
            @JvmStatic
            fun valueOf(@IdRes id: Int) = values().first { it.id == id }
        }
    }
}

class RetweetActionWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val tweetRepository: TweetRepository
) :
    CoroutineWorker(context, workerParams) {

    constructor(context: Context, workerParams: WorkerParameters) : this(
        context,
        workerParams,
        context.coreComponent().provideTweetRepository()
    )

    override suspend fun doWork(): Result {
        val tweetId = inputData.getLong(KEY_TWEET_ID, -1L)
        check(tweetId != 1L)

        return try {
            tweetRepository.retweet(tweetId)
            Result.success()
        } catch (e: RetweetError) {
            // TODO: retry
            Result.failure()
        }
    }

    companion object {
        const val KEY_TWEET_ID = "KEY_TWEET_ID"
    }
}