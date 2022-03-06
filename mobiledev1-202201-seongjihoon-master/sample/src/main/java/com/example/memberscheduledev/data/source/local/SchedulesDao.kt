package com.example.memberscheduledev.data.source.local

import androidx.room.*
import com.example.memberscheduledev.data.Schedule
import com.example.memberscheduledev.data.User

@Dao
interface SchedulesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    @Query("SELECT userid FROM user WHERE username=:username")
    suspend fun getUserId(username: String): String

    @Query("SELECT * FROM user")
    suspend fun getUserList(): List<User>

    @Query("SELECT * FROM User LIMIT 10 OFFSET (:page-1) * 10")
    suspend fun getUserListPaging(page: Int): List<User>

    @Query("SELECT * FROM user WHERE username = :username")
    suspend fun findUser(username: String): User

    @Query("SELECT * FROM schedule WHERE userid = :userid ORDER BY startdate")
    suspend fun getScheduleListForUser(userid: String): List<Schedule>

    @Query("SELECT * FROM schedule WHERE scheduleid = :scheduleid")
    suspend fun getSchedule(scheduleid: String): Schedule

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)
}