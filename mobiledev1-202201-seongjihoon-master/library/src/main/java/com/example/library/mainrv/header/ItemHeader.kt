package com.example.library.mainrv.header

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.library.R
import com.example.library.util.Utils.getDP
import com.example.library.util.Utils.getDayOfWeek
import com.example.library.mainview.DevScheduleView
import com.example.library.util.LENGTH.NAME_HEADER_WIDTH
import com.example.library.util.LENGTH.TOP_HEADER_HEIGHT
import com.example.library.util.ViewType
import java.util.*

private const val SATURDAY = 1
private const val SUNDAY = 0
private const val WEEKDAY = 2

class ItemHeader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var headerWidth = 0
    private lateinit var viewType: ViewType

    init {
        setupBaseParams()
        setupSpaceView(this@ItemHeader)
        when (viewType) {
            ViewType.DAY -> {
                for (i in 0..23) {
                    setupTimeTextView(this@ItemHeader, i)
                }
            }
            ViewType.WEEK -> {
                setupDayTextView(this)
            }
        }
    }

    private fun setupBaseParams() {
        viewType = DevScheduleView.viewType
        headerWidth = DevScheduleView.perTopHeaderWidth
        val params = LayoutParams(LayoutParams.MATCH_PARENT, TOP_HEADER_HEIGHT)
        this.layoutParams = params
        gravity = left
        orientation = HORIZONTAL
        setBackgroundColor(Color.parseColor("#CFCCC8"))
    }

    private fun setupSpaceView(view: ItemHeader) {
        val spaceView = View(context)
        spaceView.apply {
            val params = LayoutParams(NAME_HEADER_WIDTH, TOP_HEADER_HEIGHT)
            layoutParams = params
            background = ResourcesCompat.getDrawable(resources, R.drawable.border, null)
        }
        view.addView(spaceView)
    }


    private fun setupTimeTextView(view: ItemHeader, num: Int) {
        val textView = TextView(context)
        textView.apply {
            val params = LayoutParams(this@ItemHeader.headerWidth, TOP_HEADER_HEIGHT)
            layoutParams = params
            background = ResourcesCompat.getDrawable(resources, R.drawable.border, null)
            text = String.format(resources.getString(R.string.hour), num)
            setTextColor(Color.BLACK)
            setPadding(getDP(5), 0, 0, 0)
            gravity = Gravity.BOTTOM
        }
        view.addView(textView)
    }

    private fun setupDayTextView(view: ItemHeader) {
        val cal = Calendar.getInstance()
        val dayOfWeek = getDayOfWeek(DevScheduleView.currentTimeForWeek)
        cal.set(
            DevScheduleView.currentTimeForWeek.year,
            DevScheduleView.currentTimeForWeek.month - 1,
            1
        )
        val lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 0 until 14) {
            val textView = TextView(context)
            textView.apply {
                val params = LayoutParams(this@ItemHeader.headerWidth, TOP_HEADER_HEIGHT)
                layoutParams = params
                background = ResourcesCompat.getDrawable(resources, R.drawable.border, null)
                if (DevScheduleView.currentTimeForWeek.day + i > lastDay) {
                    text = if (DevScheduleView.currentTimeForWeek.month == 12)
                        String.format(
                            resources.getString(R.string.date_in_january),
                            DevScheduleView.currentTimeForWeek.day + i - lastDay
                        )
                    else
                        String.format(
                            resources.getString(R.string.date_with_month),
                            DevScheduleView.currentTimeForWeek.month + 1,
                            DevScheduleView.currentTimeForWeek.day + i - lastDay
                        )
                } else {
                    text = String.format(
                        resources.getString(R.string.date_only_day),
                        DevScheduleView.currentTimeForWeek.day + i
                    )
                }
                when (checkDayOfWeek(dayOfWeek + i)) {
                    SUNDAY -> setTextColor(Color.RED)
                    SATURDAY -> setTextColor(Color.BLUE)
                }
                setPadding(getDP(3), 0, 0, 0)
                gravity = Gravity.BOTTOM
            }
            view.addView(textView)
        }
    }

    private fun checkDayOfWeek(value: Int) = when (value % 7) {
        0 -> SATURDAY
        1 -> SUNDAY
        else -> WEEKDAY
    }
}