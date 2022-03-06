package com.example.library.mainrv.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.library.model.User

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem === newItem
    }


    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return (oldItem.id == newItem.id) &&
                (oldItem.name == newItem.name) &&
                (oldItem.scheduleList.containsAll(newItem.scheduleList))
    }

}