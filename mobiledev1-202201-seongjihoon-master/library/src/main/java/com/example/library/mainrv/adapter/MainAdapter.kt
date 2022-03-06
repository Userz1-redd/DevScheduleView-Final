package com.example.library.mainrv.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.library.listener.OnClickScheduleListener
import com.example.library.listener.OnLongClickScheduleListener
import com.example.library.mainrv.content.ContentViewHolder
import com.example.library.mainrv.content.ItemContent
import com.example.library.mainrv.content.ScheduleBinder
import com.example.library.mainrv.header.HeaderViewHolder
import com.example.library.mainrv.header.ItemHeader
import com.example.library.model.User

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_CONTENT = 1

class MainAdapter(
    private var onClickScheduleListener: OnClickScheduleListener,
    private var onLongClickScheduleListener: OnLongClickScheduleListener,
    private var expandedList: ArrayList<Int>
) :
    ListAdapter<User, RecyclerView.ViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_HEADER -> {
                return HeaderViewHolder(ItemHeader(parent.context))
            }
            else -> {
                return ContentViewHolder(ItemContent(parent.context))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HeaderViewHolder) {
            holder.bindWithView()
        } else if (holder is ContentViewHolder) {
            val scheduleBinder = ScheduleBinder(
                holder.itemView as ItemContent,
                getItem(position - 1),
                onClickScheduleListener,
                onLongClickScheduleListener,
                position, expandedList
            )
            holder.bindWithView(scheduleBinder.getBindItem())
        }
    }

    override fun getItemCount(): Int {
        return currentList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_HEADER
            else -> VIEW_TYPE_CONTENT
        }
    }

    fun submitExpandedList(list: ArrayList<Int>) {
        expandedList = list
    }

    fun getHeaderView(list: RecyclerView): View {
        return ItemHeader(list.context)
    }

    fun setList(list: MutableList<User>) {
        val pagingList = ArrayList<User>(currentList)
        pagingList.addAll(list)
        submitList(pagingList)
    }

    fun setOnClickScheduleListener(listener: OnClickScheduleListener) {
       this.onClickScheduleListener = listener
    }
}