package com.droibit.looking2.core.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.droibit.looking2.core.databinding.ListItemActionBinding
import com.droibit.looking2.core.databinding.ListItemActionTitleBinding
import com.droibit.looking2.core.ui.view.ActionMenu
import com.droibit.looking2.core.ui.view.ShapeAwareContentPadding
import java.util.Objects

typealias ActionItemClickListener = (ActionItemListAdapter.ActionItem) -> Unit

class ActionItemListAdapter(
    context: Context,
    title: String? = null,
    private val items: MutableList<ActionItem>,
    private val itemClickListener: ActionItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ActionItem(
        @get:IdRes val id: Int,
        val icon: Drawable,
        val text: String
    ) {
        override fun toString() = "ActionItem(id=$id, text='$text')"
    }

    var title: String? = title
        set(value) {
            if (Objects.equals(title, value)) {
                return
            }
            val oldValue = field
            field = value

            when {
                oldValue == null -> notifyItemInserted(0)
                value == null -> notifyItemRemoved(0)
                else -> notifyItemChanged(0)
            }
        }

    private val inflater = LayoutInflater.from(context)

    private val hasTitle get() = !title.isNullOrEmpty()

    private val itemPadding = ShapeAwareContentPadding(context)

    constructor(
        context: Context,
        title: String? = null,
        @MenuRes menuRes: Int,
        itemClickListener: ActionItemClickListener
    ) :
        this(context, title, inflateActionMenu(context, menuRes), itemClickListener)

    // RecyclerView.Adapter

    override fun getItemCount() = items.size + if (hasTitle) 1 else 0

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is HeaderViewHolder -> {
                holder.itemView.updatePadding(
                    left = 0,
                    top = itemPadding.firstItemTopPx,
                    right = 0,
                    bottom = holder.itemView.paddingBottom
                )
                holder.bind(checkNotNull(title))
            }
            is ActionViewHolder -> {
                holder.itemView.updatePadding(
                    left = itemPadding.leftPx,
                    top = if (position == 0) itemPadding.firstItemTopPx else 0,
                    right = itemPadding.rightPx,
                    bottom = if (position == itemCount - 1) itemPadding.lastItemBottomPx else 0
                )
                val titleAwarePosition = if (hasTitle) position - 1 else position
                holder.bind(items[titleAwarePosition])
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        when (ViewType.of(viewType)) {
            ViewType.HEADER -> {
                HeaderViewHolder(
                    binding = ListItemActionTitleBinding.inflate(inflater, parent, false)
                )
            }
            ViewType.ACTION_ITEM -> {
                ActionViewHolder(
                    binding = ListItemActionBinding.inflate(inflater, parent, false)
                ).apply {
                    itemView.setOnClickListener {
                        val titleAwarePosition = adapterPosition - if (hasTitle) 1 else 0
                        itemClickListener.invoke(items[titleAwarePosition])
                    }
                }
            }
        }

    override fun getItemViewType(position: Int): Int =
        if (hasTitle && position == 0) ViewType.HEADER.id else ViewType.ACTION_ITEM.id
}

private enum class ViewType(val id: Int) {
    HEADER(id = 0),
    ACTION_ITEM(id = 1);

    companion object {
        fun of(id: Int) = values().first { it.id == id }
    }
}

private class HeaderViewHolder(private val binding: ListItemActionTitleBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(title: String) {
        binding.title = title
    }
}

private class ActionViewHolder(private val binding: ListItemActionBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ActionItemListAdapter.ActionItem) {
        binding.item = item
    }
}

private fun inflateActionMenu(
    context: Context,
    @MenuRes menuRes: Int
): MutableList<ActionItemListAdapter.ActionItem> {
    val menu = ActionMenu(context).also {
        MenuInflater(context).inflate(menuRes, it)
    }
    return (0 until menu.size())
        .asSequence()
        .map { menu.getItem(it) }
        .map { ActionItemListAdapter.ActionItem(it.itemId, it.icon, it.title.toString()) }
        .toMutableList()
}
