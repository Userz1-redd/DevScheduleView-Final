package com.example.library.namerv.name

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.library.R
import com.example.library.mainview.DevScheduleView
import com.example.library.util.LENGTH.NAME_HEADER_WIDTH

class ItemName @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    lateinit var nameTextView: TextView
    private var perLeftHeaderHeight = 0

    init {
        setupBaseParams()
        setupNameTextView()
    }

    private fun setupBaseParams() {
        perLeftHeaderHeight = DevScheduleView.perLeftHeaderHeight
        val params = LayoutParams(NAME_HEADER_WIDTH, perLeftHeaderHeight)
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
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
        }
        this.addView(nameTextView)
    }
}
