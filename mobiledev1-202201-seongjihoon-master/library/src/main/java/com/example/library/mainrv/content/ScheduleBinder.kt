package com.example.library.mainrv.content

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Dimension
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.size
import com.example.library.R
import com.example.library.listener.OnClickScheduleListener
import com.example.library.listener.OnLongClickScheduleListener
import com.example.library.mainview.DevScheduleView
import com.example.library.model.Schedule
import com.example.library.model.User
import com.example.library.util.DayScheduleType
import com.example.library.util.LENGTH.DEFAULT_WEEKVIEW_HEIGHT
import com.example.library.util.LENGTH.TOP_HEADER_HEIGHT
import com.example.library.util.Utils.checkEndTime
import com.example.library.util.Utils.compareTime
import com.example.library.util.Utils.compareTimeMinutes
import com.example.library.util.Utils.convert1DigitNum
import com.example.library.util.Utils.getDP
import com.example.library.util.ViewType
import com.example.library.util.WeekScheduleType
import java.util.concurrent.ConcurrentHashMap

class ScheduleBinder(
    private val itemContent: ItemContent,
    private val item: User, private val onClickScheduleListener: OnClickScheduleListener,
    private val onLongClickScheduleListener: OnLongClickScheduleListener,
    private val position: Int,
    private val expandedList: ArrayList<Int>
) : View.OnClickListener, View.OnLongClickListener {
    private var scheduleListView = ArrayList<TextView>()
    private var idx = -1
    private var indexMap = ConcurrentHashMap<Int, Int>()
    private var schedulePosition = IntArray(101) { -1 }
    private var overScheduleNum = IntArray(14) { 0 }
    private var scheduleLimit = 100

    init {

        setupHeight()

        setupSchedule(item)

        setupListener()

        addNumOfScheduleView()

    }

    private fun setupHeight() {
        if (expandedList.contains(position - 1)) {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                DevScheduleView.perLeftHeaderHeight * 2
            )
            itemContent.layoutParams = layoutParams
        }
    }

    private fun setupSchedule(item: User) {
        scheduleLimit = getScheduleLimit()
        when (DevScheduleView.viewType) {
            ViewType.DAY -> {
                for (s in item.scheduleList) {
                    checkEndTime(s)
                    idx++
                    when (getDayScheduleType(s)) {
                        DayScheduleType.INDAY -> itemContent.frameLayout.addView(
                            addDayViewSchedule(s, 1)
                        )
                        DayScheduleType.STARTDAY -> itemContent.frameLayout.addView(
                            addDayViewSchedule(s, 2)
                        )
                        DayScheduleType.BETWEEN -> itemContent.frameLayout.addView(
                            addDayViewSchedule(s, 3)
                        )
                        DayScheduleType.ENDDAY -> itemContent.frameLayout.addView(
                            addDayViewSchedule(s, 4)
                        )
                    }
                }
            }
            ViewType.WEEK -> {
                for (s in item.scheduleList) {
                    checkEndTime(s)
                    idx++
                    when (getWeekScheduleType(s)) {
                        WeekScheduleType.IN_DAY -> {
                            if (itemContent.weekLayoutList[compareTime(
                                    DevScheduleView.currentTimeForWeek,
                                    s.startDate
                                ).toInt()].size >= scheduleLimit
                            ) {
                                overScheduleNum[compareTime(
                                    DevScheduleView.currentTimeForWeek,
                                    s.startDate
                                ).toInt()]++
                            } else {
                                itemContent.weekLayoutList[compareTime(
                                    DevScheduleView.currentTimeForWeek,
                                    s.startDate
                                ).toInt()]
                                    .addView(addWeekViewSchedule(s, 1, 0))
                            }
                        }
                        WeekScheduleType.IN_TWOWEEK -> {
                            for (i in compareTime(
                                DevScheduleView.currentTimeForWeek,
                                s.startDate
                            )..compareTime(DevScheduleView.currentTimeForWeek, s.endDate)) {
                                if (itemContent.weekLayoutList[i.toInt()].size >= scheduleLimit) {
                                    overScheduleNum[i.toInt()]++
                                } else {
                                    itemContent.weekLayoutList[i.toInt()]
                                        .addView(addWeekViewSchedule(s, 2, i.toInt()))
                                }
                            }
                        }
                        WeekScheduleType.OVER_ENDDAY -> {
                            for (i in compareTime(
                                DevScheduleView.currentTimeForWeek,
                                s.startDate
                            )..13) {
                                if (itemContent.weekLayoutList[i.toInt()].size >= scheduleLimit) {
                                    overScheduleNum[i.toInt()]++
                                } else {
                                    itemContent.weekLayoutList[i.toInt()]
                                        .addView(addWeekViewSchedule(s, 3, i.toInt()))
                                }
                            }
                        }
                        WeekScheduleType.OVER_STARTDAY -> {
                            for (i in 0..compareTime(
                                DevScheduleView.currentTimeForWeek,
                                s.endDate
                            )) {
                                if (itemContent.weekLayoutList[i.toInt()].size >= scheduleLimit) {
                                    overScheduleNum[i.toInt()]++
                                } else {
                                    itemContent.weekLayoutList[i.toInt()]
                                        .addView(addWeekViewSchedule(s, 4, i.toInt()))
                                }
                            }
                        }
                        WeekScheduleType.OVER_BOTH -> {
                            for (i in 0..13) {
                                if (itemContent.weekLayoutList[i].size >= scheduleLimit) {
                                    overScheduleNum[i]++
                                } else {
                                    itemContent.weekLayoutList[i]
                                        .addView(addWeekViewSchedule(s, 0, i))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupListener() {
        itemContent.setOnClickListener(this)
        itemContent.setOnLongClickListener(this)
        for (i in scheduleListView) {
            i.setOnClickListener(this)
            i.setOnLongClickListener(this)
        }
    }

    private fun addNumOfScheduleView() {
        for (i in 0..13) {
            if (overScheduleNum[i] > 0) {
                val numView = TextView(itemContent.context)
                numView.apply {
                    val params = LinearLayout.LayoutParams(
                        DevScheduleView.perTopHeaderWidth,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.marginEnd = getDP(2)
                    layoutParams = params
                    setTextSize(Dimension.DP, 30F)
                    setTextColor(Color.BLACK)
                    text = String.format(
                        resources.getString(R.string.over_num_text),
                        overScheduleNum[i]
                    )
                }
                itemContent.weekLayoutList[i].addView(numView)
            }
        }
    }

    private fun getScheduleLimit() =
        (((DevScheduleView.viewHeight - TOP_HEADER_HEIGHT) / (DevScheduleView.visibleNumberOfMembers + 1)) * 2 / DEFAULT_WEEKVIEW_HEIGHT) - 1

    private fun getDayScheduleType(s: Schedule): DayScheduleType {
        if (compareTime(DevScheduleView.currentTimeForDay, s.startDate) == 0L) {
            return if (compareTime(s.startDate, s.endDate) == 0L) {
                DayScheduleType.INDAY
            } else {
                DayScheduleType.STARTDAY
            }
        } else if (compareTime(
                DevScheduleView.currentTimeForDay,
                s.startDate
            ) < 0 && compareTime(DevScheduleView.currentTimeForDay, s.endDate) > 0
        ) {
            return DayScheduleType.BETWEEN
        } else if (compareTime(
                DevScheduleView.currentTimeForDay,
                s.startDate
            ) < 0 && compareTime(DevScheduleView.currentTimeForDay, s.endDate) == 0L
        ) {
            return DayScheduleType.ENDDAY
        }
        return DayScheduleType.ELSE
    }

    private fun getWeekScheduleType(s: Schedule): WeekScheduleType {
        if (
            compareTime(s.startDate, s.endDate) == 0L &&
            compareTime(DevScheduleView.currentTimeForWeek, s.startDate) < 14 &&
            compareTime(DevScheduleView.currentTimeForWeek, s.startDate) >= 0
        ) {
            return WeekScheduleType.IN_DAY
        } else if (
            compareTime(s.startDate, s.endDate) != 0L &&
            compareTime(DevScheduleView.currentTimeForWeek, s.startDate) < 14 &&
            compareTime(DevScheduleView.currentTimeForWeek, s.startDate) >= 0 &&
            compareTime(DevScheduleView.currentTimeForWeek, s.endDate) < 14
        ) {
            return WeekScheduleType.IN_TWOWEEK
        } else if (
            compareTime(s.startDate, s.endDate) != 0L &&
            compareTime(DevScheduleView.currentTimeForWeek, s.startDate) < 14 &&
            compareTime(DevScheduleView.currentTimeForWeek, s.startDate) >= 0 &&
            compareTime(DevScheduleView.currentTimeForWeek, s.endDate) >= 14
        ) {
            return WeekScheduleType.OVER_ENDDAY
        } else if (
            compareTime(s.startDate, s.endDate) != 0L &&
            compareTime(DevScheduleView.currentTimeForWeek, s.startDate) < 0 &&
            compareTime(DevScheduleView.currentTimeForWeek, s.endDate) < 14 &&
            compareTime(DevScheduleView.currentTimeForWeek, s.endDate) >= 0
        ) {
            return WeekScheduleType.OVER_STARTDAY
        } else if (
            compareTime(DevScheduleView.currentTimeForWeek, s.startDate) < 0 &&
            compareTime(DevScheduleView.currentTimeForWeek, s.endDate) >= 14
        ) {
            return WeekScheduleType.OVER_BOTH
        }
        return WeekScheduleType.ELSE
    }

    private fun addDayViewSchedule(s: Schedule, case: Int): TextView {
        val scheduleHeight = when {
            DevScheduleView.perLeftHeaderHeight / 2 >= getDP(50) -> getDP(50)
            DevScheduleView.perLeftHeaderHeight / 2 < getDP(25) -> getDP(25)
            else -> DevScheduleView.perLeftHeaderHeight / 2
        }
        val textView = getTextView(s, case, scheduleHeight)
        indexMap[scheduleListView.size] = idx
        scheduleListView.add(textView)
        return textView
    }

    @SuppressLint("SetTextI18n")
    private fun addWeekViewSchedule(s: Schedule, case: Int, cnt: Int): TextView {
        val testView = TextView(itemContent.context)
        testView.apply {
            val params = LinearLayout.LayoutParams(
                DevScheduleView.perTopHeaderWidth,
                DEFAULT_WEEKVIEW_HEIGHT
            )
            params.marginEnd = getDP(2)
            layoutParams = params
            val startHour = convert1DigitNum(s.startDate.hour)
            val startMinute = convert1DigitNum(s.startDate.minute)
            val endHour = convert1DigitNum(s.endDate.hour)
            val endMinute = convert1DigitNum(s.endDate.minute)
            if (case == 1) {
                text = String.format(
                    resources.getString(R.string.week_full_text),
                    startHour,
                    startMinute,
                    endHour,
                    endMinute,
                    s.name
                )

            } else {
                text = when {
                    compareTime(
                        DevScheduleView.currentTimeForWeek,
                        s.startDate
                    ) == cnt.toLong() -> {
                        String.format(
                            resources.getString(R.string.week_start_text),
                            startHour,
                            startMinute,
                            s.name
                        )
                    }
                    compareTime(
                        DevScheduleView.currentTimeForWeek,
                        s.endDate
                    ) == cnt.toLong() -> {
                        String.format(
                            resources.getString(R.string.week_end_text),
                            endHour,
                            endMinute,
                            s.name
                        )
                    }
                    else -> {
                        String.format(resources.getString(R.string.week_allday_text),s.name)
                    }
                }
            }
            if (text.length > 27) {
                text = text.substring(0, 25) + resources.getString(R.string.ellipsis)
            }
            setTextSize(Dimension.DP, 30F)
            setTextColor(Color.BLACK)
            background = ResourcesCompat.getDrawable(resources, R.drawable.border_perweekitem, null)
        }
        indexMap[scheduleListView.size] = idx
        scheduleListView.add(testView)
        return testView
    }

    private fun getMarginPosition(index: Int): Int {
        for (i in 0..100) {
            if (schedulePosition[i] == -1) {
                schedulePosition[i] = index
                return i
            }
        }
        return 0
    }

    private fun updateSchedulePosition(index: Int) {
        for (i in 0..100) {
            if (schedulePosition[i] != -1) {
                val compareSchedule = item.scheduleList[schedulePosition[i]]
                val currentSchedule = item.scheduleList[index]
                if (!(compareTimeMinutes(
                        currentSchedule.startDate,
                        compareSchedule.startDate
                    ) <= 0 &&
                            compareTimeMinutes(
                                currentSchedule.startDate,
                                compareSchedule.endDate
                            ) >= 0
                            )
                ) {
                    schedulePosition[i] = -1
                }
            }
        }
    }

    private fun getTextView(s: Schedule, case: Int, scheduleHeight: Int): TextView {
        val textView = TextView(itemContent.context)
        textView.apply {
            updateSchedulePosition(idx)
            val position = getMarginPosition(idx)
            val params: LinearLayout.LayoutParams =
                when (case) {
                    1 -> LinearLayout.LayoutParams(
                        compareTimeMinutes(
                            s.startDate,
                            s.endDate
                        ).toInt() * DevScheduleView.perTopHeaderWidth / 60,
                        scheduleHeight
                    )
                    2, 3 -> LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        scheduleHeight
                    )
                    else -> LinearLayout.LayoutParams(
                        ((s.endDate.hour + s.endDate.minute.toFloat() / 60) * DevScheduleView.perTopHeaderWidth).toInt(),
                        scheduleHeight
                    )
                }
            params.marginStart =
                when (case) {
                    1, 2 -> ((s.startDate.hour + s.startDate.minute.toFloat() / 60) * DevScheduleView.perTopHeaderWidth).toInt()
                    else -> 0
                }
            params.topMargin =
                scheduleHeight * position
            layoutParams = params
            text = s.name
            setTextColor(Color.BLACK)
            background = ResourcesCompat.getDrawable(
                resources,
                R.drawable.border_perweekitem, null
            )
        }
        return textView
    }

    fun getBindItem(): User {
        return this.item
    }

    override fun onClick(v: View) {
        if (v in scheduleListView) {
            onClickScheduleListener.onClickSchedule(
                position - 1,
                indexMap.getOrDefault(scheduleListView.indexOf(v), -1)
            )
        } else {
            onClickScheduleListener.onClickEmptySchedule()
        }
    }

    override fun onLongClick(v: View): Boolean {
        if (v in scheduleListView) {
            onLongClickScheduleListener.onLongClick(
                position - 1,
                indexMap.getOrDefault(scheduleListView.indexOf(v), -1), v
            )
        }
        return false
    }
}
