package com.droibit.looking2.account.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.droibit.looking2.account.databinding.ListItemAccountBinding
import com.droibit.looking2.core.model.account.TwitterAccount
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding

class AccountListAdapter(
    private val inflater: LayoutInflater,
    private val itemPadding: ShapeAwareContentPadding,
    private val itemClickListener: (TwitterAccount) -> Unit
) : RecyclerView.Adapter<AccountListAdapter.ItemViewHolder>() {

    private val accounts = mutableListOf<TwitterAccount>()

    fun setAccounts(accounts: List<TwitterAccount>) {
        this.accounts.clear()
        this.accounts.addAll(accounts)
        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int = accounts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            binding = ListItemAccountBinding.inflate(inflater, parent, false)
        ).apply {
            itemView.updatePadding(
                left = itemPadding.leftPx,
                right = itemPadding.rightPx
            )
            itemView.setOnClickListener {
                itemClickListener(accounts[adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(accounts[position])
    }

    class ItemViewHolder(
        private val binding: ListItemAccountBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TwitterAccount) {
            binding.item = item
        }
    }
}