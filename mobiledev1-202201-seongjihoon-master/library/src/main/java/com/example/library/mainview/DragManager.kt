package com.example.library.mainview

import android.content.ClipData
import android.content.ClipDescription
import android.os.Handler
import android.os.Looper
import android.view.DragEvent
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.library.R
import com.example.library.listener.OnScheduleDropListener
import com.example.library.mainrv.adapter.MainAdapter
import com.example.library.model.User

import com.example.library.util.LENGTH.NAME_HEADER_WIDTH
import com.example.library.util.LENGTH.TOP_HEADER_HEIGHT
import com.example.library.util.Utils.compareTimeMinutes
import com.example.library.util.Utils.getDP
import com.example.library.util.Utils.getDays
import com.example.library.util.Utils.moveDayTimeTo
import com.example.library.util.Utils.moveWeekTimeTo
import com.example.library.util.ViewType

class DragManager(
    private val devScheduleView: DevScheduleView,
    private val coverMainRvHorizontalView: HorizontalScrollView,
    private val mainRecyclerView: RecyclerView,
    private val mainAdapter: MainAdapter,
    private val expandedPositionList: ArrayList<Int>,
    private var scheduleDropListener : OnScheduleDropListener,
    private val scrollListenerFromMainRv : RecyclerView.OnScrollListener
) {
    private lateinit var indicatorView: TextView
    private var dragState = false
    private var position = -1
    private var scheduleIdx = -1
    private var currentPositionX = -1
    private var currentPositionY = -1


    fun setupDragListener(){
        devScheduleView.setOnDragListener { v, e ->
            when (e.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    dragState = true
                    if (e.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.invalidate()
                        true
                    } else {
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DRAG_LOCATION -> {
                    currentPositionX =
                        ((coverMainRvHorizontalView.scrollX + e.x - NAME_HEADER_WIDTH) / DevScheduleView.perTopHeaderWidth).toInt()
                    currentPositionY = countPositionY(
                        DevScheduleView.saveScrollY + e.y
                                - TOP_HEADER_HEIGHT
                    )
                    if (currentPositionY < mainAdapter.currentList.size) {
                        dragState = true
                        setupIndicator(
                            currentPositionX,
                            currentPositionY,
                            indicatorView
                        )
                        if (e.x >= DevScheduleView.perTopHeaderWidth * 3.55) {
                            coverMainRvHorizontalView.smoothScrollBy(getDP(10), 0)
                        } else if (e.x <= 50 && e.y != 0F) {
                            coverMainRvHorizontalView.smoothScrollBy(-getDP(10), 0)
                        } else if (e.x != 0F && e.y <= 50) {
                            mainRecyclerView.smoothScrollBy(0, -getDP(50))
                        } else if (e.x != 0F && e.y >= DevScheduleView.perLeftHeaderHeight * DevScheduleView.visibleNumberOfMembers) {
                            mainRecyclerView.smoothScrollBy(0, getDP(50))
                        }
                    } else {
                        indicatorView.text = devScheduleView.context.resources.getString(R.string.indicator_out_text)
                        dragState = false
                    }
                    true
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    if (dragState) {
                        actionDrop(
                            position,
                            scheduleIdx,
                            currentPositionX,
                            currentPositionY,
                        )
                        currentPositionX = 0
                        currentPositionY = 0
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> {
                    v.invalidate()
                    devScheduleView.removeView(indicatorView)
                    true
                }
                else -> false
            }
        }
    }

    fun startDragAndDrop(position : Int, scheduleIdx : Int,clickedView : View, viewCreator: ViewCreator ) {
        val item = ClipData.Item(clickedView.tag as? CharSequence)
        val dragData = ClipData(
            clickedView.tag as? CharSequence,
            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN),
            item
        )
        val myShadow = View.DragShadowBuilder(clickedView)
        currentPositionX = 0
        currentPositionY = 0
        this.position = position
        this.scheduleIdx = scheduleIdx
        indicatorView = viewCreator.getIndicatorView()
        devScheduleView.addView(indicatorView)
        clickedView.startDragAndDrop(dragData, myShadow, null, 0)
    }

    fun setScheduleDropListener(listener : OnScheduleDropListener){
        scheduleDropListener = listener
    }

    private fun countPositionY(value: Float): Int {
        var position = 0
        var temp = value
        while (true) {
            temp -= if (expandedPositionList.contains(position)) {
                if (temp - DevScheduleView.perLeftHeaderHeight * 2 < 0) {
                    break
                }
                DevScheduleView.perLeftHeaderHeight * 2
            } else {
                if (temp - DevScheduleView.perLeftHeaderHeight < 0) {
                    break
                }
                DevScheduleView.perLeftHeaderHeight
            }
            position++
        }
        return position
    }

    private fun setupIndicator(positionX: Int, positionY: Int, view: TextView) {
        when (DevScheduleView.viewType) {
            ViewType.DAY -> {
                if (positionY < mainAdapter.currentList.size)
                    view.text = String.format(
                        devScheduleView.context.resources.getString(R.string.indicator_day_text),
                        positionX,
                        mainAdapter.currentList[positionY].name
                    )
            }
            ViewType.WEEK -> {
                if (positionY < mainAdapter.currentList.size) {
                    val time = getDays(DevScheduleView.currentTimeForWeek, positionX)
                    view.text = String.format(
                        devScheduleView.context.resources.getString(R.string.indicator_week_text),
                        time.month,
                        time.day,
                        mainAdapter.currentList[positionY].name
                    )
                }
            }
        }
    }

    private fun actionDrop(
        position: Int,
        scheduleIdx: Int,
        currentPositionX: Int,
        currentPositionY: Int
    ) {
        if (mainRecyclerView.scrollState != 2) {
            mainRecyclerView.clearOnScrollListeners()
            val changeUserId = mainAdapter.currentList[currentPositionY].id
            val currentSchedule = mainAdapter.currentList[position].scheduleList[scheduleIdx]
            val diff = compareTimeMinutes(currentSchedule.startDate, currentSchedule.endDate)
            when (DevScheduleView.viewType) {
                ViewType.DAY -> {
                    val changeTime = getDays(DevScheduleView.currentTimeForDay, 0)
                    changeTime.hour = currentPositionX
                    changeTime.minute = 0
                    val changeSchedule = moveDayTimeTo(currentSchedule, changeTime, diff)
                    scheduleDropListener.onScheduleDrop(changeUserId, changeSchedule)
                    val currentList = mainAdapter.currentList
                    currentList[position].scheduleList.remove(currentSchedule)
                    currentList[currentPositionY].scheduleList.add(changeSchedule)
                    currentList[currentPositionY].scheduleList.sortWith(
                        compareBy({ it.startDate.year },
                            { it.startDate.month },
                            { it.startDate.day },
                            { it.startDate.hour },
                            { it.startDate.minute })
                    )
                    updateView(currentList)
                }
                ViewType.WEEK -> {
                    val changeTime =
                        getDays(DevScheduleView.currentTimeForWeek, currentPositionX)
                    changeTime.hour = currentSchedule.startDate.hour
                    changeTime.minute = currentSchedule.startDate.minute
                    val changeSchedule = moveWeekTimeTo(currentSchedule, changeTime)
                    scheduleDropListener.onScheduleDrop(changeUserId, changeSchedule)
                    val currentList = mainAdapter.currentList
                    currentList[position].scheduleList.remove(currentSchedule)
                    currentList[currentPositionY].scheduleList.add(changeSchedule)
                    currentList[currentPositionY].scheduleList.sortWith(
                        compareBy({ it.startDate.year },
                            { it.startDate.month },
                            { it.startDate.day },
                            { it.startDate.hour },
                            { it.startDate.minute })
                    )
                    updateView(currentList)
                }
            }
        }
    }

    private fun updateView(list: List<User>) {
        mainRecyclerView.removeAllViewsInLayout()
        mainAdapter.submitList(list)
        mainRecyclerView.adapter = mainAdapter
        mainRecyclerView.scrollBy(0, DevScheduleView.saveScrollY)
        Handler(Looper.getMainLooper()).postDelayed({
            mainRecyclerView.addOnScrollListener(scrollListenerFromMainRv)
        }, 100)

    }
}