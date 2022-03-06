package com.example.library.namerv.name

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.library.listener.OnClickNameHeaderListener

@SuppressLint("ClickableViewAccessibility")
class NameViewHolder(
    val itemName: ItemName,
    nameHeaderListener: OnClickNameHeaderListener
) : RecyclerView.ViewHolder(itemName),
    View.OnTouchListener {
    private var nameHeaderClickListener: OnClickNameHeaderListener = nameHeaderListener

    init {
        itemName.setOnTouchListener(this)
    }

    fun bindWithView(item: String) {
        itemName.nameTextView.text = item
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (v) {
            itemName -> {
                nameHeaderClickListener.onClickNameHeader(absoluteAdapterPosition)
            }
        }
        return false
    }
}