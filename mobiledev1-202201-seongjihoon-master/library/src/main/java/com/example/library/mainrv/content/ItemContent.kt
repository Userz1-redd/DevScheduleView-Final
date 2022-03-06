package com.example.library.mainrv.content

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.library.R
import com.example.library.mainview.DevScheduleView
import com.example.library.util.LENGTH.NAME_HEADER_WIDTH
import com.example.library.util.ViewType

class ItemContent @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    var weekLayoutList = ArrayList<LinearLayout>()
    private lateinit var nameTextView: TextView
    var frameLayout = FrameLayout(context)

    init {
        setupBaseParams()
        setupNameTextView()
        when (DevScheduleView.viewType) {
            ViewType.DAY -> setupDayScheduleLayout(this)
            ViewType.WEEK -> setupWeekScheduleLayout(this)
        }
    }

    private fun setupBaseParams() {
        val params = LayoutParams(LayoutParams.MATCH_PARENT, DevScheduleView.perLeftHeaderHeight)
        this.layoutParams = params
        gravity = left
        orientation = HORIZONTAL
        background = ResourcesCompat.getDrawable(resources, R.drawable.border, null)
    }

    private fun setupNameTextView() {
        nameTextView = TextView(context)
        nameTextView.apply {
            val params = LayoutParams(NAME_HEADER_WIDTH, LayoutParams.MATCH_PARENT)
            layoutParams = params
        }
        this.addView(nameTextView)
    }

    private fun setupWeekScheduleLayout(view: ItemContent) {
        for (i in 1..14) {
            val weekLayout = LinearLayout(context)
            weekLayout.apply {
                val params =
                    LayoutParams(DevScheduleView.perTopHeaderWidth, LayoutParams.MATCH_PARENT)
                this.layoutParams = params
                orientation = VERTICAL
                background = ResourcesCompat.getDrawable(resources, R.drawable.border, null)
            }
            view.addView(weekLayout)
            weekLayoutList.add(weekLayout)
        }
    }

    private fun setupDayScheduleLayout(view: ItemContent) {
        frameLayout.apply {
            val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            this.layoutParams = params
        }
        view.addView(frameLayout)
    }
}