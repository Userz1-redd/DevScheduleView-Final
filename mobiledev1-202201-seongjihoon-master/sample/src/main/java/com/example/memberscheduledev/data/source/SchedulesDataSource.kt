package com.example.memberscheduledev.data.source

import com.example.memberscheduledev.data.Schedule
import com.example.memberscheduledev.data.User


interface SchedulesDataSource {
    interface LoadSchedulesCallback {

        fun onSchedulesLoaded(scheduleList: List<Schedule>)

        fun onDataNotAvailable()
    }

    interface GetScheduleCallback {
        fun onScheduleLoaded(schedule: Schedule)

        fun onDataNotAvailable()
    }

    interface GetUserListCallback {
        fun onUserListLoaded(userList: List<User>)

        fun onDataNotAvailable()
    }

    suspend fun getUserListPaging(callback: GetUserListCallback, page: Int)

    suspend fun getUserList(callback: GetUserListCallback)

    suspend fun getScheduleList(userid: String, callback: LoadSchedulesCallback)

    suspend fun getSchedule(scheduleId: String, callback: GetScheduleCallback)

    suspend fun addSchedule(username: String, schedule: Schedule)

    suspend fun modifySchedule(schedule: Schedule)

    suspend fun deleteSchedule(schedule: Schedule)

}