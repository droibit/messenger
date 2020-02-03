package com.droibit.looking2.timeline.ui.content.mylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.size.Scale
import com.droibit.looking2.core.model.tweet.UserList
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import com.droibit.looking2.timeline.R
import com.droibit.looking2.timeline.databinding.ListItemMyListBinding
import javax.inject.Provider

class MyListAdapter(
    private val inflater: LayoutInflater,
    private val itemPadding: ShapeAwareContentPadding,
    private val lifecycleOwner: Provider<LifecycleOwner>,
    private val itemClickListener: (UserList) -> Unit
) : RecyclerView.Adapter<MyListAdapter.ViewHolder>() {

    private val myLists = mutableListOf<UserList>()

    override fun getItemCount(): Int = myLists.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            lifecycleOwner.get(),
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
}