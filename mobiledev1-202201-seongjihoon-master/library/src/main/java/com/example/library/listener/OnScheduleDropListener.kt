package com.example.library.listener

import com.example.library.model.Schedule

interface OnScheduleDropListener {
    fun onScheduleDrop(changeUserId : String, schedule : Schedule)
}