package com.example.library.namerv.adapter

import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.ListAdapter
import com.example.library.listener.OnClickNameHeaderListener
import com.example.library.namerv.name.ItemName
import com.example.library.namerv.name.NameViewHolder
import com.example.library.mainview.DevScheduleView

class NameAdapter(private var onClickNameHeaderListener: OnClickNameHeaderListener) :
    ListAdapter<String, NameViewHolder>(NameDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NameViewHolder {
        return NameViewHolder(ItemName(parent.context), onClickNameHeaderListener)
    }

    override fun onBindViewHolder(holder: NameViewHolder, position: Int) {
        if (position == 0) {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                DevScheduleView.perLeftHeaderHeight * 2
            )
            holder.itemName.layoutParams = layoutParams
        }
        holder.bindWithView(getItem(position)!!)

    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    fun setList(list: MutableList<String>) {
        val pagingList = ArrayList<String>(currentList)
        pagingList.addAll(list)
        submitList(pagingList)
    }

    fun setOnClickNameHeaderListener(listener: OnClickNameHeaderListener) {
        this.onClickNameHeaderListener = listener
    }
}