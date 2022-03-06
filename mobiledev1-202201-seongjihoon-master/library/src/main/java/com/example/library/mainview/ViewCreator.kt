package com.example.library.mainview

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.annotation.Dimension
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library.R
import com.example.library.util.LENGTH.NAME_HEADER_WIDTH
import com.example.library.util.LENGTH.TOP_HEADER_HEIGHT
import com.example.library.util.Utils.getDP

class ViewCreator(private val context: Context) {

    fun getExpandAnimator(): ValueAnimator {
        return ValueAnimator
            .ofInt(DevScheduleView.perLeftHeaderHeight, DevScheduleView.perLeftHeaderHeight * 2)
            .setDuration(300)
    }

    fun getCancelAnimator(): ValueAnimator {
        return ValueAnimator
            .ofInt(DevScheduleView.perLeftHeaderHeight * 2, DevScheduleView.perLeftHeaderHeight)
            .setDuration(300)
    }

    fun getCoverHorizontalScrollView(): HorizontalScrollView {
        val coverMainRvHorizontalView = HorizontalScrollView(context)
        coverMainRvHorizontalView.apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        return coverMainRvHorizontalView
    }

    fun getMainRecyclerView(): RecyclerView {
        val mainRecyclerView = RecyclerView(context)
        mainRecyclerView.apply {
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager
            scrollToPosition(0)
        }
        return mainRecyclerView
    }

    fun getNameRecyclerView(): RecyclerView {
        val nameRecyclerView = RecyclerView(context)
        nameRecyclerView.apply {
            val params: RecyclerView.LayoutParams =
                RecyclerView.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
            params.setMargins(0, TOP_HEADER_HEIGHT, 0, 0)
            layoutParams = params
            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager
        }
        return nameRecyclerView
    }

    fun getHeaderSpaceView(): View {
        val headerSpaceView = View(context)
        headerSpaceView.apply {
            val params =
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, TOP_HEADER_HEIGHT)
            layoutParams = params
        }
        return headerSpaceView
    }

    fun getTopSpaceView(): TextView {
        val spaceView = TextView(context)
        spaceView.apply {
            val params = FrameLayout.LayoutParams(NAME_HEADER_WIDTH, TOP_HEADER_HEIGHT)
            layoutParams = params
            setTextSize(Dimension.DP, 30F)
            setTextColor(Color.BLACK)
            textAlignment = TEXT_ALIGNMENT_CENTER
            background = ResourcesCompat.getDrawable(resources, R.drawable.border, null)
        }
        return spaceView
    }

    fun getIndicatorView(): TextView {
        val spaceView = TextView(context)
        spaceView.apply {
            val params = FrameLayout.LayoutParams(getDP(100), getDP(50))
            layoutParams = params
            setTextSize(Dimension.DP, 30F)
            setTextColor(Color.BLACK)
            gravity = Gravity.CENTER
            background = ResourcesCompat.getDrawable(resources, R.drawable.border, null)
        }
        return spaceView
    }

    @SuppressLint("ClickableViewAccessibility")
    fun getDisableTouchListener() = object : RecyclerView.OnItemTouchListener {
        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return true
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            //onTouchEvent
        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            //onRequestDisallowInterceptTouchEvent
        }
    }
}