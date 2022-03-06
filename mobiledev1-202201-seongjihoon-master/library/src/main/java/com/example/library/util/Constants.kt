package com.example.library.util

import com.example.library.util.Utils.getDP

object LENGTH{
    val DEFAULT_WIDTH = getDP(100)
    val DEFAULT_HEIGHT = getDP(100)
    const val DEFAULT_VISIBLE_TOP_HEADER =  3.608365019F
    val DEFAULT_WEEKVIEW_HEIGHT = getDP(40)
    val TOP_HEADER_HEIGHT = getDP(25)
    val NAME_HEADER_WIDTH = getDP(50)
    const val START_OF_SCROLL = 0
}

object STATE{
    const val NOT_SCROLLED = 0
    const val SCROLL_ATLEAST_ONCE = 1
}

enum class ViewType {
    DAY, WEEK
}

enum class DayScheduleType{
    INDAY,STARTDAY,BETWEEN,ENDDAY, ELSE
}

enum class WeekScheduleType{
    IN_DAY,IN_TWOWEEK,OVER_ENDDAY,OVER_STARTDAY,OVER_BOTH, ELSE
}

