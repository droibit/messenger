package com.droibit.looking2.account.ui.twitter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.droibit.looking2.account.databinding.ListItemAccountBinding
import com.droibit.looking2.account.databinding.ListItemAccountFooterBinding
import com.droibit.looking2.account.databinding.ListItemAccountHeaderBinding
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.ui.common.view.ShapeAwareContentPadding

class TwitterAccountListAdapter(
    private val itemPadding: ShapeAwareContentPadding,
    private val itemClickListener: OnItemClickListener
) : ListAdapter<TwitterAccount, TwitterAccountListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            binding = ListItemAccountBinding.inflate(inflater, parent, false)
        ).apply {
            itemView.updatePadding(
                left = itemPadding.leftPx,
                right = itemPadding.rightPx
            )
            itemView.setOnClickListener {
                itemClickListener.onAccountItemClick(getItem(bindingAdapterPosition))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(item = getItem(position))
    }

    class ViewHolder(
        private val binding: ListItemAccountBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TwitterAccount) {
            binding.item = item
        }
    }

    fun interface OnItemClickListener {
        @UiThread
        fun onAccountItemClick(account: TwitterAccount)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TwitterAccount>() {
            override fun areItemsTheSame(
                oldItem: TwitterAccount,
                newItem: TwitterAccount
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TwitterAccount,
                newItem: TwitterAccount
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class TwitterAccountListHeaderAdapter(
    private val itemPadding: ShapeAwareContentPadding,
) : RecyclerView.Adapter<TwitterAccountListHeaderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            binding = ListItemAccountHeaderBinding.inflate(inflater, parent, false).apply {
                this.contentPadding = itemPadding
            }
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = Unit

    override fun getItemCount(): Int = 1

    class ViewHolder(binding: ListItemAccountHeaderBinding) : RecyclerView.ViewHolder(binding.root)
}

class TwitterAccountListFooterAdapter(
    private val itemPadding: ShapeAwareContentPadding,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<TwitterAccountListFooterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            binding = ListItemAccountFooterBinding.inflate(inflater, parent, false).apply {
                this.contentPadding = itemPadding
            }
        ).apply {
            itemView.setOnClickListener {
                itemClickListener.onAccountAddClick()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = Unit

    override fun getItemCount(): Int = 1

    fun interface OnItemClickListener {
        @UiThread
        fun onAccountAddClick()
    }

    class ViewHolder(binding: ListItemAccountFooterBinding) : RecyclerView.ViewHolder(binding.root)
}
