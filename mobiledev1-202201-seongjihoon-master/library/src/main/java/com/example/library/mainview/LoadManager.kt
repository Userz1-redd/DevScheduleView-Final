package com.example.library.mainview

import android.os.Handler
import android.os.Looper
import android.widget.HorizontalScrollView
import androidx.recyclerview.widget.RecyclerView
import com.example.library.mainrv.adapter.MainAdapter
import com.example.library.util.LoadingDialog
import com.example.library.util.Utils.getDays

class LoadManager(
    private val loadingDialog: LoadingDialog,
    private val scrollListenerFromMainRv: RecyclerView.OnScrollListener
) {

    fun loadLaterWeekSchedule(
        mainRecyclerView: RecyclerView, coverMainRvHorizontalView:
        HorizontalScrollView,
        mainAdapter: MainAdapter, expandedPositionList: ArrayList<Int>, saveScrollY: Int
    ) {
        mainRecyclerView.clearOnScrollListeners()
        loadingDialog.show()
        coverMainRvHorizontalView.scrollTo(DevScheduleView.perTopHeaderWidth * 4, 0)
        DevScheduleView.currentTimeForWeek = getDays(DevScheduleView.currentTimeForWeek, 6)
        mainRecyclerView.removeAllViewsInLayout()
        mainAdapter.submitExpandedList(expandedPositionList)
        mainRecyclerView.adapter = mainAdapter
        Handler(Looper.getMainLooper()).postDelayed({
            coverMainRvHorizontalView.scrollTo(DevScheduleView.perTopHeaderWidth * 4, 0)
            loadingDialog.dismiss()
        }, 300)
        Handler(Looper.getMainLooper()).postDelayed({
            mainRecyclerView.scrollBy(0, saveScrollY)
            mainRecyclerView.clearOnScrollListeners()
            Handler(Looper.getMainLooper()).postDelayed({
                mainRecyclerView.addOnScrollListener(scrollListenerFromMainRv)
            }, 100)
        }, 100)
    }

    fun loadPriorWeekSchedule(
        mainRecyclerView: RecyclerView, coverMainRvHorizontalView:
        HorizontalScrollView,
        mainAdapter: MainAdapter, expandedPositionList: ArrayList<Int>, saveScrollY: Int
    ) {
        mainRecyclerView.clearOnScrollListeners()
        loadingDialog.show()
        coverMainRvHorizontalView.scrollTo(DevScheduleView.perTopHeaderWidth * 7, 0)
        DevScheduleView.currentTimeForWeek = getDays(DevScheduleView.currentTimeForWeek, -7)
        mainRecyclerView.removeAllViewsInLayout()
        mainAdapter.submitExpandedList(expandedPositionList)
        mainRecyclerView.adapter = mainAdapter
        Handler(Looper.getMainLooper()).postDelayed({
            coverMainRvHorizontalView.scrollTo(DevScheduleView.perTopHeaderWidth * 7, 0)
            loadingDialog.dismiss()
        }, 300)
        Handler(Looper.getMainLooper()).postDelayed({
            mainRecyclerView.scrollBy(0, saveScrollY)
            mainRecyclerView.clearOnScrollListeners()
            Handler(Looper.getMainLooper()).postDelayed({
                mainRecyclerView.addOnScrollListener(scrollListenerFromMainRv)
            }, 100)
        }, 100)
    }

    fun loadPriorDaySchedule(
        mainRecyclerView: RecyclerView, coverMainRvHorizontalView:
        HorizontalScrollView,
        mainAdapter: MainAdapter, expandedPositionList: ArrayList<Int>, saveScrollY: Int
    ) {
        mainRecyclerView.clearOnScrollListeners()
        loadingDialog.show()
        coverMainRvHorizontalView.scrollTo(DevScheduleView.perTopHeaderWidth * 10, 0)
        DevScheduleView.currentTimeForDay = getDays(DevScheduleView.currentTimeForDay, -1)
        mainRecyclerView.removeAllViewsInLayout()
        mainAdapter.submitExpandedList(expandedPositionList)
        mainRecyclerView.adapter = mainAdapter
        Handler(Looper.getMainLooper()).postDelayed({
            coverMainRvHorizontalView.scrollTo(DevScheduleView.perTopHeaderWidth * 10, 0)
            loadingDialog.dismiss()
        }, 300)
        Handler(Looper.getMainLooper()).postDelayed({
            mainRecyclerView.scrollBy(0, saveScrollY)
            mainRecyclerView.clearOnScrollListeners()
            Handler(Looper.getMainLooper()).postDelayed({
                mainRecyclerView.addOnScrollListener(scrollListenerFromMainRv)
            }, 100)
        }, 100)
    }

    fun loadLaterDaySchedule(
        mainRecyclerView: RecyclerView, coverMainRvHorizontalView:
        HorizontalScrollView,
        mainAdapter: MainAdapter, expandedPositionList: ArrayList<Int>, saveScrollY: Int
    ) {
        mainRecyclerView.clearOnScrollListeners()
        loadingDialog.show()
        coverMainRvHorizontalView.scrollTo(DevScheduleView.perTopHeaderWidth * 10, 0)
        DevScheduleView.currentTimeForDay = getDays(DevScheduleView.currentTimeForDay, 1)
        mainRecyclerView.removeAllViewsInLayout()
        mainAdapter.submitExpandedList(expandedPositionList)
        mainRecyclerView.adapter = mainAdapter
        Handler(Looper.getMainLooper()).postDelayed({
            coverMainRvHorizontalView.scrollTo(DevScheduleView.perTopHeaderWidth * 10, 0)
            loadingDialog.dismiss()
        }, 300)
        Handler(Looper.getMainLooper()).postDelayed({
            mainRecyclerView.scrollBy(0, saveScrollY)
            mainRecyclerView.clearOnScrollListeners()
            Handler(Looper.getMainLooper()).postDelayed({
                mainRecyclerView.addOnScrollListener(scrollListenerFromMainRv)
            }, 100)
        }, 100)
    }

}