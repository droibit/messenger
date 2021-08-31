package com.droibit.looking2.timeline.ui.content.mylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.size.Scale
import com.droibit.looking2.core.model.tweet.UserList
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.databinding.ListItemMyListBinding
import com.droibit.looking2.ui.common.view.ShapeAwareContentPadding
import javax.inject.Provider

class MyListAdapter(
    private val itemPadding: ShapeAwareContentPadding,
    private val lifecycleOwner: Provider<LifecycleOwner>,
    private val itemClickListener: OnItemClickListener
) : ListAdapter<UserList, MyListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            lifecycleOwner.get(),
            binding = ListItemMyListBinding.inflate(inflater, parent, false)
        ).apply {
            itemView.setOnClickListener {
                itemClickListener.onUserListClick(getItem(bindingAdapterPosition))
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
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val lifecycleOwner: LifecycleOwner,
        private val binding: ListItemMyListBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(myList: UserList) {
            binding.listIcon.load(myList.user.profileUrl) {
                error(R.drawable.ic_user_icon_circle)
                placeholder(R.drawable.ic_user_icon_circle)
                scale(Scale.FIT)
                lifecycle(lifecycleOwner)
            }
            binding.userList = myList
        }
    }

    fun interface OnItemClickListener {
        fun onUserListClick(myList: UserList)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserList>() {
            override fun areItemsTheSame(oldItem: UserList, newItem: UserList): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserList, newItem: UserList): Boolean {
                return oldItem == newItem
            }
        }
    }
}
