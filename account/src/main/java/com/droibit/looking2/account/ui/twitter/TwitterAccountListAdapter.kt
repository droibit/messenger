package com.droibit.looking2.account.ui.twitter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.droibit.looking2.account.databinding.ListItemAccountBinding
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding

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
