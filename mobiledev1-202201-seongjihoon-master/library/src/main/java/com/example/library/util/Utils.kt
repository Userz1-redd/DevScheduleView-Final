package com.example.library.util

import android.content.res.Resources
import com.example.library.model.Schedule
import com.example.library.model.Time
import java.util.*

object Utils {
    fun getDP(value: Int): Int {
        return (value * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
    }

    fun compareTime(currentTime: Time, compareTime: Time): Long {
        val beginDay = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentTime.year)
            set(Calendar.MONTH, currentTime.month - 1)
            set(Calendar.DAY_OF_MONTH, currentTime.day)
        }.timeInMillis
        val endDay = Calendar.getInstance().apply {
            set(Calendar.YEAR, compareTime.year)
            set(Calendar.MONTH, compareTime.month - 1)
            set(Calendar.DAY_OF_MONTH, compareTime.day)
        }.timeInMillis
        return (getIgnoredTimeDays(endDay) - getIgnoredTimeDays(beginDay)) / (24 * 60 * 60 * 1000)
    }

    private fun getIgnoredTimeDays(time: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun compareTimeMinutes(currentTime: Time, compareTime: Time): Long {
        val beginDay = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentTime.year)
            set(Calendar.MONTH, currentTime.month - 1)
            set(Calendar.DAY_OF_MONTH, currentTime.day)
            set(Calendar.HOUR_OF_DAY, currentTime.hour)
            set(Calendar.MINUTE, currentTime.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val endDay = Calendar.getInstance().apply {
            set(Calendar.YEAR, compareTime.year)
            set(Calendar.MONTH, compareTime.month - 1)
            set(Calendar.DAY_OF_MONTH, compareTime.day)
            set(Calendar.HOUR_OF_DAY, compareTime.hour)
            set(Calendar.MINUTE, compareTime.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        return (endDay - beginDay) / (60 * 1000)
    }

    fun convert1DigitNum(num: Int) = if (num < 10) "0${num}" else num.toString()

    fun getDayOfWeek(time: Time): Int {
        val cal = Calendar.getInstance()
        cal.set(time.year, time.month - 1, time.day)
        return cal.get(Calendar.DAY_OF_WEEK)
    }

    fun getDays(time: Time, day: Int): Time {
        val c = Calendar.getInstance()
        c.set(time.year, time.month - 1, time.day)
        c.add(Calendar.DATE, day)
        return Time(
            c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DATE), 0, 0, ""
        )
    }

    private fun plusMinutes(time: Time, minute: Int): Time {
        val c = Calendar.getInstance()
        c.set(time.year, time.month - 1, time.day)
        c.set(Calendar.HOUR_OF_DAY, time.hour)
        c.set(Calendar.MINUTE, time.minute)
        c.add(Calendar.MINUTE, minute)
        return Time(
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH) + 1,
            c.get(Calendar.DATE),
            c.get(Calendar.HOUR_OF_DAY),
            c.get(Calendar.MINUTE),
            ""
        )
    }

    fun moveDayTimeTo(schedule: Schedule, time: Time, diff: Long): Schedule {
        val endDate = plusMinutes(time.copy(), diff.toInt())
        return Schedule(schedule.id, schedule.name, time, endDate)
    }

    fun moveWeekTimeTo(schedule: Schedule, time: Time): Schedule {
        val difference = compareTimeMinutes(schedule.startDate, time).toInt()
        val endDate = plusMinutes(schedule.endDate, difference)
        schedule.startDate = time.copy()
        schedule.endDate = endDate
        return schedule
    }

    fun checkEndTime(s : Schedule){
        if(s.endDate.hour==0&&s.endDate.minute==0){
            s.endDate = plusMinutes(s.endDate,-1)
        }
    }
}