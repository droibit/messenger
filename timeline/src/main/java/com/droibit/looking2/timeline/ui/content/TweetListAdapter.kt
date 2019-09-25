package com.droibit.looking2.timeline.ui.content

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.ui.view.ListItemPadding
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.databinding.ListItemTweetBinding
import com.squareup.picasso.Picasso

class TweetListAdapter(
    context: Context,
    private val tweetTextProcessor: TweetTextProcessor,
    private val itemClickListener: (Tweet) -> Unit
) : RecyclerView.Adapter<TweetListAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private val itemPadding = ListItemPadding(context)

    private val tweets = mutableListOf<Tweet>()

    var lastClickPosition: Int = RecyclerView.NO_POSITION
        private set

    operator fun get(index: Int) = tweets[index]

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            binding = ListItemTweetBinding.inflate(inflater, parent, false)
        ).apply {
            itemView.setOnLongClickListener {
                lastClickPosition = adapterPosition
                itemClickListener.invoke(tweets[adapterPosition]); true
            }
        }
    }

    override fun getItemCount() = tweets.size

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.itemView.setPadding(
            itemPadding.leftPx,
            if (position == 0) itemPadding.firstItemTopPx else 0,
            itemPadding.rightPx,
            if (position == itemCount - 1) itemPadding.lastItemBottomPx else 0
        )
        holder.bind(tweetTextProcessor, tweets[position], position)
    }

    fun add(tweets: List<Tweet>) {
        val positionStart = this.tweets.size
        this.tweets.addAll(tweets)
        this.notifyItemRangeInserted(positionStart, tweets.size)
    }

    class ViewHolder(private val binding: ListItemTweetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // @get:ColorInt
        // private val darkBackgroundColor by bindColor(R.color.colorDarkerBackground)
        //
        // @get:ColorInt
        // private val lighterBackgroundColor by bindColor(R.color.colorLighterBackground)

        fun bind(
            tweetTextProcessor: TweetTextProcessor,
            srcTweet: Tweet,
            position: Int
        ) {
            val tweet = srcTweet.retweetedTweet?.let { it } ?: srcTweet
            // itemView.setBackgroundColor(
            //     if (position % 2 == 0) darkBackgroundColor else lighterBackgroundColor
            // )

            Picasso.get()
                .load(tweet.user.profileUrl)
                .error(R.drawable.ic_account_circle)
                .placeholder(R.drawable.ic_account_circle)
                .fit()
                .tag(TAG_TWEET_PHOTO)
                .into(binding.userIcon)

            binding.userName.text = tweet.user.name
            binding.tweetText.apply {
                tweetTextProcessor.processTweetText(this, tweet)
                this.isVisible = this.text.isNotBlank()
            }

            binding.quotedTweetView.apply {
                val quotedTweet = tweet.quotedTweet
                this.isVisible = if (quotedTweet == null) false else {
                    binding.quotedUserName.text = quotedTweet.user.name
                    tweetTextProcessor.processQuotedTweetText(binding.quotedTweetText, quotedTweet)
                    true
                }
            }
            binding.retweetBy.apply {
                this.isVisible = if (srcTweet.retweetedTweet == null) false else {
                    text = context.getString(R.string.timeline_tweet_retweet_by, srcTweet.user.name)
                    true
                }
            }

            binding.photoIcon.isVisible = tweet.hasPhotoUrl
            binding.createdAt.text = DateUtils.getRelativeTimeSpanString(
                srcTweet.createdAt,
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS
            )
        }
    }

    companion object {

        const val TAG_TWEET_PHOTO = "TAG_TWEET_PHOTO"
    }
}