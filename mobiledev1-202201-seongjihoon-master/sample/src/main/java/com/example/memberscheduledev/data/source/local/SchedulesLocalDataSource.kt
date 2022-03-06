package com.example.memberscheduledev.data.source.local

import com.example.memberscheduledev.data.Schedule
import com.example.memberscheduledev.data.User
import com.example.memberscheduledev.data.source.SchedulesDataSource

class SchedulesLocalDataSource(
    private val schedulesDao: SchedulesDao
) : SchedulesDataSource {
    override suspend fun getUserListPaging(
        callback: SchedulesDataSource.GetUserListCallback,
        page: Int
    ) {
        val userList = schedulesDao.getUserListPaging(page)
        if (userList.isNotEmpty()) {
            callback.onUserListLoaded(userList)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override suspend fun getUserList(callback: SchedulesDataSource.GetUserListCallback) {
        val userList = schedulesDao.getUserList()
        if (userList.isNotEmpty()) {
            callback.onUserListLoaded(userList)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override suspend fun getScheduleList(
        userid: String,
        callback: SchedulesDataSource.LoadSchedulesCallback
    ) {
        val scheduleList = schedulesDao.getScheduleListForUser(userid)
        if (!scheduleList.isEmpty()) {
            callback.onSchedulesLoaded(scheduleList)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override suspend fun getSchedule(
        scheduleId: String,
        callback: SchedulesDataSource.GetScheduleCallback
    ) {
        val schedule = schedulesDao.getSchedule(scheduleId)
        if (schedule != null) {
            callback.onScheduleLoaded(schedule)
        } else {
            callback.onDataNotAvailable()
        }
    }

    override suspend fun addSchedule(username: String, schedule: Schedule) {
        if (schedulesDao.findUser(username) == null) {
            schedulesDao.insertUser(User(username))
        }
        val userid = schedulesDao.getUserId(username)
        schedule.userId = userid
        schedulesDao.insertSchedule(schedule)
    }

    override suspend fun modifySchedule(schedule: Schedule) {
        schedulesDao.updateSchedule(schedule)
    }

    override suspend fun deleteSchedule(schedule: Schedule) {
        schedulesDao.deleteSchedule(schedule)
    }

}