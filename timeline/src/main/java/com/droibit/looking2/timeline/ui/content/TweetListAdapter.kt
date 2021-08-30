package com.droibit.looking2.timeline.ui.content

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.droibit.looking2.core.model.tweet.Tweet
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.databinding.ListItemTweetBinding
import javax.inject.Provider

class TweetListAdapter(
    private val itemPadding: ShapeAwareContentPadding,
    private val lifecycleOwner: Provider<LifecycleOwner>,
    private val tweetTextProcessor: TweetTextProcessor,
    private val itemClickListener: OnItemClickListener
) : ListAdapter<Tweet, TweetListAdapter.ViewHolder>(DIFF_CALLBACK) {

    operator fun get(index: Int): Tweet = getItem(index)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            lifecycleOwner.get(),
            binding = ListItemTweetBinding.inflate(inflater, parent, false)
        ).apply {
            itemView.setOnLongClickListener {
                itemClickListener.onTweetClick(getItem(bindingAdapterPosition)); true
            }
        }
    }

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
        holder.bind(tweetTextProcessor, srcTweet = getItem(position))
    }

    class ViewHolder(
        private val lifecycleOwner: LifecycleOwner,
        private val binding: ListItemTweetBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            tweetTextProcessor: TweetTextProcessor,
            srcTweet: Tweet
        ) {
            val tweet = srcTweet.retweetedTweet ?: srcTweet
            binding.userIcon.load(tweet.user.profileUrl) {
                placeholder(R.drawable.ic_user_icon_circle)
                error(R.drawable.ic_user_icon_circle)
                scale(Scale.FIT)
                lifecycle(lifecycleOwner)
            }

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

    fun interface OnItemClickListener {
        fun onTweetClick(tweet: Tweet)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Tweet>() {
            override fun areItemsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Tweet, newItem: Tweet): Boolean {
                return oldItem == newItem
            }
        }
    }
}
