package com.example.memberscheduledev.data.source

import com.example.memberscheduledev.data.Schedule
import com.example.memberscheduledev.data.User
import com.example.memberscheduledev.data.source.local.SchedulesLocalDataSource

class SchedulesRepository(private val local: SchedulesLocalDataSource) : SchedulesDataSource {
    override suspend fun getUserListPaging(
        callback: SchedulesDataSource.GetUserListCallback,
        page: Int
    ) {
        local.getUserListPaging(object : SchedulesDataSource.GetUserListCallback {
            override fun onUserListLoaded(userList: List<User>) {
                callback.onUserListLoaded(userList)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

        }, page)
    }

    override suspend fun getUserList(callback: SchedulesDataSource.GetUserListCallback) {
        local.getUserList(object : SchedulesDataSource.GetUserListCallback {
            override fun onUserListLoaded(userList: List<User>) {
                callback.onUserListLoaded(userList)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

        })
    }


    override suspend fun getScheduleList(
        userid: String,
        callback: SchedulesDataSource.LoadSchedulesCallback
    ) {
        local.getScheduleList(userid, object : SchedulesDataSource.LoadSchedulesCallback {
            override fun onSchedulesLoaded(scheduleList: List<Schedule>) {
                callback.onSchedulesLoaded(scheduleList)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override suspend fun getSchedule(
        scheduleId: String,
        callback: SchedulesDataSource.GetScheduleCallback
    ) {
        local.getSchedule(scheduleId, object : SchedulesDataSource.GetScheduleCallback {
            override fun onScheduleLoaded(schedule: Schedule) {
                callback.onScheduleLoaded(schedule)
            }

            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }
        })
    }

    override suspend fun addSchedule(username: String, schedule: Schedule) {
        local.addSchedule(username, schedule)
    }

    override suspend fun modifySchedule(schedule: Schedule) {
        local.modifySchedule(schedule)
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        local.deleteSchedule(schedule)
    }

    companion object {

        private var INSTANCE: SchedulesRepository? = null

        @JvmStatic
        fun getInstance(local: SchedulesLocalDataSource) =
            INSTANCE ?: synchronized(SchedulesRepository::class.java) {
                INSTANCE ?: SchedulesRepository(local)
                    .also { INSTANCE = it }
            }

        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }
}