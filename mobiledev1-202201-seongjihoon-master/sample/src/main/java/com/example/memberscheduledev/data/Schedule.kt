package com.example.memberscheduledev.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.library.model.Schedule
import com.example.library.model.Time
import java.util.*

@Entity(
    tableName = "schedule"
)
data class Schedule(
    @ColumnInfo(name = "schedulename") var name: String = "",
    @ColumnInfo(name = "startdate") var startDate: String,
    @ColumnInfo(name = "enddate") var endDate: String
) {
    @ColumnInfo(name = "userid")
    var userId: String = ""

    @PrimaryKey
    @ColumnInfo(name = "scheduleid")
    var id: String = UUID.randomUUID().toString()

    fun toScheduleTime(): Schedule {
        var parseString = startDate.split(".")
        val sTime = Time(
            parseString[0].toInt(),
            parseString[1].toInt(),
            parseString[2].toInt(),
            parseString[3].toInt(),
            parseString[4].toInt(),
            ""
        )
        parseString = endDate.split(".")
        val eTime = Time(
            parseString[0].toInt(),
            parseString[1].toInt(),
            parseString[2].toInt(),
            parseString[3].toInt(),
            parseString[4].toInt(),
            ""
        )
        return Schedule(id, name, sTime, eTime)
    }
}