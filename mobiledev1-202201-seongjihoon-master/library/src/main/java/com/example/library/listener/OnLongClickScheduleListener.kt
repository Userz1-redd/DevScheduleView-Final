package com.example.library.listener
import android.view.View

interface OnLongClickScheduleListener {
    fun onLongClick(position: Int, scheduleIdx: Int, clickedView : View)
}