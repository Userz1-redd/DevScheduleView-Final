package com.example.memberscheduledev.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.memberscheduledev.data.Schedule
import com.example.memberscheduledev.data.User

@Database(entities = arrayOf(User::class, Schedule::class), version = 2, exportSchema = false)
abstract class SchedulesDatabase : RoomDatabase() {
    abstract fun schedulesDao(): SchedulesDao

    companion object {
        @Volatile
        private var INSTANCE: SchedulesDatabase? = null

        private val lock = Any()
        fun getDatabase(context: Context): SchedulesDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        SchedulesDatabase::class.java, "schedules.db"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                }
                return INSTANCE!!
            }

        }
    }

}