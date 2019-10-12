package com.droibit.looking2.timeline.ui.content.mylist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.droibit.looking2.core.model.tweet.UserList
import com.droibit.looking2.core.ui.view.ListItemPadding
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.databinding.ListItemMyListBinding
import com.squareup.picasso.Picasso

class MyListAdapter(
    context: Context,
    private val itemClickListener: (UserList) -> Unit
) : RecyclerView.Adapter<MyListAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private val itemPadding = ListItemPadding(context)

    private val myLists = mutableListOf<UserList>()

    override fun getItemCount(): Int = myLists.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            binding = ListItemMyListBinding.inflate(inflater, parent, false)
        ).apply {
            itemView.setOnClickListener {
                itemClickListener.invoke(myLists[adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setPadding(
            itemPadding.leftPx,
            if (position == 0) itemPadding.firstItemTopPx else 0,
            itemPadding.rightPx,
            if (position == itemCount - 1) itemPadding.lastItemBottomPx else 0
        )
        holder.bind(myLists[position])
    }

    fun setMyLists(myLists: List<UserList>) {
        this.myLists.clear()
        this.myLists.addAll(myLists)
        this.notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ListItemMyListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(myList: UserList) {
            Picasso.get()
                .load(myList.user.profileUrl)
                .error(R.drawable.ic_account_circle)
                .placeholder(R.drawable.ic_account_circle)
                .fit()
                .tag(TAG_LIST_USER_ICON)
                .into(binding.listIcon)
            binding.userList = myList
        }
    }

    companion object {

        const val TAG_LIST_USER_ICON = "TAG_LIST_USER_ICON"
    }
}