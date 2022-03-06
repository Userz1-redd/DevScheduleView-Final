package com.example.library.mainview

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExpandManager(
    private var mainRecyclerView: RecyclerView, private var nameRecyclerView: RecyclerView,
    private var disableTouchListener: RecyclerView.OnItemTouchListener,
    private var expandedPositionList: ArrayList<Int>, private var viewCreator: ViewCreator
) {
    private lateinit var slideAnimator: ValueAnimator
    private lateinit var cancelAnimator: ValueAnimator

    init {
        setupAnimator()
    }


    private fun setupAnimator() {
        slideAnimator = viewCreator.getExpandAnimator()
        cancelAnimator = viewCreator.getCancelAnimator()
    }

    fun setupExpandAnimation(
        position: Int,
        isLoadLaterSchedule: Boolean,
        isLoadPriorSchedule: Boolean
    ) {
        if (!isLoadLaterSchedule && !isLoadPriorSchedule) {
            val firstVisiblePosition =
                (nameRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val lastVisiblePosition =
                (nameRecyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            val firstCompletelyVisiblePosition =
                (nameRecyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            var visibleExpandPosition = -1
            for (i in firstVisiblePosition..lastVisiblePosition) {
                if (expandedPositionList.contains(i)) {
                    visibleExpandPosition = i
                    break
                }
            }
            if (visibleExpandPosition == position) {
                //for disable
            } else if (firstVisiblePosition + 1 == firstCompletelyVisiblePosition && visibleExpandPosition == firstVisiblePosition) {
                //for disable
            } else if (visibleExpandPosition != -1) {
                nameRecyclerView.addOnItemTouchListener(disableTouchListener)
                cancelAndExpandAnimation(visibleExpandPosition, position)
                Handler(Looper.getMainLooper()).postDelayed({
                    nameRecyclerView.removeOnItemTouchListener(disableTouchListener)
                }, 400)
            } else {
                nameRecyclerView.addOnItemTouchListener(disableTouchListener)
                startExpandAnimation(position)
                Handler(Looper.getMainLooper()).postDelayed({
                    nameRecyclerView.removeOnItemTouchListener(disableTouchListener)
                }, 400)
            }
        }
    }

    private fun startExpandAnimation(position: Int) {
        if (mainRecyclerView.scrollState != 2) {
            cancelAnimator.removeAllUpdateListeners()
            val mainItemView =
                mainRecyclerView.findViewHolderForAdapterPosition(position + 1)?.itemView
            val nameItemView =
                nameRecyclerView.findViewHolderForAdapterPosition(position)?.itemView
            slideAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                mainItemView!!.layoutParams.height = value
                nameItemView!!.layoutParams.height = value
                mainItemView.requestLayout()
                nameItemView.requestLayout()
            }
            slideAnimator.interpolator = AccelerateDecelerateInterpolator()
            slideAnimator.start()
            expandedPositionList.add(position)
        }
    }

    private fun cancelAndExpandAnimation(expandedPosition: Int, position: Int) {
        if (mainRecyclerView.scrollState != 2) {
            cancelAnimator.removeAllUpdateListeners()
            slideAnimator.removeAllUpdateListeners()
            val mainItemView =
                mainRecyclerView.findViewHolderForAdapterPosition(position + 1)?.itemView
            val nameItemView =
                nameRecyclerView.findViewHolderForAdapterPosition(position)?.itemView
            slideAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                mainItemView!!.layoutParams.height = value
                nameItemView!!.layoutParams.height = value
                mainItemView.requestLayout()
                nameItemView.requestLayout()
            }
            slideAnimator.interpolator = AccelerateDecelerateInterpolator()
            val nameExpandedItemView =
                nameRecyclerView.findViewHolderForAdapterPosition(expandedPosition)?.itemView
            val mainExpandedItemView =
                mainRecyclerView.findViewHolderForAdapterPosition(expandedPosition + 1)?.itemView
            cancelAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                nameExpandedItemView!!.layoutParams.height = value
                nameExpandedItemView.requestLayout()
                mainExpandedItemView!!.layoutParams.height = value
                mainExpandedItemView.requestLayout()
            }
            cancelAnimator.interpolator = AccelerateDecelerateInterpolator()
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(slideAnimator, cancelAnimator)
            animatorSet.start()
            expandedPositionList.remove(expandedPosition)
            expandedPositionList.add(position)
        }
    }

}