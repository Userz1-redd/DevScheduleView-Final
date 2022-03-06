package com.example.library.mainview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.library.R
import com.example.library.listener.*
import com.example.library.util.Utils.getDays
import com.example.library.namerv.adapter.NameAdapter
import com.example.library.mainrv.adapter.MainAdapter
import com.example.library.mainrv.adapter.StickyHeaderItemDecoration
import com.example.library.model.Time
import com.example.library.model.User
import com.example.library.util.LoadingDialog
import java.util.*
import kotlin.collections.ArrayList
import com.example.library.model.Schedule
import com.example.library.util.LENGTH.DEFAULT_HEIGHT
import com.example.library.util.LENGTH.DEFAULT_VISIBLE_TOP_HEADER
import com.example.library.util.LENGTH.DEFAULT_WIDTH
import com.example.library.util.LENGTH.NAME_HEADER_WIDTH
import com.example.library.util.LENGTH.START_OF_SCROLL
import com.example.library.util.LENGTH.TOP_HEADER_HEIGHT
import com.example.library.util.STATE.NOT_SCROLLED
import com.example.library.util.STATE.SCROLL_ATLEAST_ONCE
import com.example.library.util.ViewType


class DevScheduleView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private lateinit var coverMainRvHorizontalView: HorizontalScrollView
    private lateinit var mainRecyclerView: RecyclerView
    private lateinit var mainAdapter: MainAdapter
    private lateinit var nameRecyclerView: RecyclerView
    private lateinit var nameAdapter: NameAdapter
    private lateinit var headerSpaceView: View
    private lateinit var topSpaceView: TextView
    private lateinit var scrollListenerFromMainRv: RecyclerView.OnScrollListener
    private lateinit var pagingScrollListener: EndlessRecyclerViewScrollListener
    private val loadingDialog = LoadingDialog(context)
    private var isLoadPriorSchedule = false
    private var isLoadLaterSchedule = false
    private var expandedPositionList = ArrayList<Int>()
    private var horizontalScrollState = NOT_SCROLLED
    private val viewCreator = ViewCreator(context)
    private lateinit var dragManager : DragManager
    private val disableTouchListener = viewCreator.getDisableTouchListener()
    private lateinit var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener
    private lateinit var dateChangeListener: OnDateChangeListener
    private lateinit var pageChangeListener: OnPageChangeListener
    private lateinit var scheduleLongClickListener: OnLongClickScheduleListener
    private lateinit var scheduleClickListener: OnClickScheduleListener
    private lateinit var nameHeaderClickListener: OnClickNameHeaderListener
    private lateinit var timeHeaderClickListener: OnClickTimeHeaderListener
    private lateinit var scheduleDropListener: OnScheduleDropListener

    companion object {
        internal var viewHeight = 0
        internal var viewType = ViewType.DAY
        internal var perTopHeaderWidth = DEFAULT_WIDTH
        internal var perLeftHeaderHeight = DEFAULT_HEIGHT
        internal var visibleNumberOfMembers = 0
        internal var mActiveFeature = 0
        internal var saveScrollY = 0
        internal lateinit var currentTimeForDay: Time
        internal lateinit var currentTimeForWeek: Time
    }

    init {
        if (attrs != null) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.DevScheduleView)
            viewType = when(a.getInt(R.styleable.DevScheduleView_viewType, 0)){
                0 -> ViewType.DAY
                else -> ViewType.WEEK
            }
            visibleNumberOfMembers = a.getInt(R.styleable.DevScheduleView_visibleNumberOfMembers, 5)
            a.recycle()
        }

        setupDefaultListener()

        measureHeight()

        setupHorizontalScrollView()

        setupMainRecyclerView()

        setupNameRecyclerView()

        setupPagingScrollListener()

        setupHeaderSpaceView()

        setupTopSpaceView()

        setupObserveScroll()

        setupDisableScroll()

        setupSnappingEffect(coverMainRvHorizontalView)

        setupInfiniteHorizontalScroll(coverMainRvHorizontalView)

        setupDragListener()

    }

    private fun setupDefaultListener() {
        globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
            if (this@DevScheduleView.height > 0) {
                viewHeight = this@DevScheduleView.height
            }
            perLeftHeaderHeight =
                (this@DevScheduleView.height - TOP_HEADER_HEIGHT) / (visibleNumberOfMembers + 1)
            perTopHeaderWidth =
                ((this@DevScheduleView.width - NAME_HEADER_WIDTH).toFloat() / DEFAULT_VISIBLE_TOP_HEADER).toInt()
        }

        dateChangeListener = object : OnDateChangeListener {
            override fun OnDateChange(date: Time) {
                //dateChangeListener
            }
        }

        pageChangeListener = object : OnPageChangeListener {
            override fun onPageChange(page: Int) {
                //pageChangeListener
            }
        }

        scheduleLongClickListener =
            object : OnLongClickScheduleListener {
                override fun onLongClick(position: Int, scheduleIdx: Int, clickedView: View) {
                    dragManager.startDragAndDrop(position,scheduleIdx,clickedView,viewCreator)
                }
            }

        scheduleClickListener = object : OnClickScheduleListener {
            override fun onClickSchedule(position: Int, scheduleIdx: Int) {
                //Schedule Clicked
            }

            override fun onClickEmptySchedule() {
                //EmptySchedule Clicked
            }
        }

        nameHeaderClickListener =
            object : OnClickNameHeaderListener {
                @SuppressLint("ClickableViewAccessibility")
                override fun onClickNameHeader(position: Int) {
                    val expandManager = ExpandManager(
                        mainRecyclerView,
                        nameRecyclerView,
                        disableTouchListener,
                        expandedPositionList,
                        viewCreator
                    )
                    expandManager.setupExpandAnimation(
                        position,
                        isLoadLaterSchedule,
                        isLoadPriorSchedule
                    )
                }
            }
        timeHeaderClickListener =
            object : OnClickTimeHeaderListener {
                override fun onClickTimeHeader() {
                    //TimeHeader Clicked
                }
            }

        scheduleDropListener =
            object : OnScheduleDropListener {
                override fun onScheduleDrop(changeUserId: String, schedule: Schedule) {
                    //Schedule dropped
                }
            }
    }

    private fun measureHeight() {
        this@DevScheduleView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
    }

    private fun setupHorizontalScrollView() {
        coverMainRvHorizontalView = viewCreator.getCoverHorizontalScrollView()
        this.addView(coverMainRvHorizontalView)
    }

    private fun setupMainRecyclerView() {
        mainRecyclerView = viewCreator.getMainRecyclerView()
        mainAdapter =
            MainAdapter(scheduleClickListener, scheduleLongClickListener, expandedPositionList)
        mainRecyclerView.apply {
            adapter = this@DevScheduleView.mainAdapter
            addItemDecoration(StickyHeaderItemDecoration(getSectionCallback()))
        }
        coverMainRvHorizontalView.addView(mainRecyclerView)
    }

    private fun setupNameRecyclerView() {
        nameRecyclerView = viewCreator.getNameRecyclerView()
        nameAdapter = NameAdapter(nameHeaderClickListener)
        nameRecyclerView.apply {
            adapter = this@DevScheduleView.nameAdapter
        }
        this.addView(nameRecyclerView)
    }

    private fun setupPagingScrollListener() {
        pagingScrollListener = object :
            EndlessRecyclerViewScrollListener(mainRecyclerView.layoutManager as LinearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                pageChangeListener.onPageChange(page)
                if (page == 1 || page == 2) {
                    resetScroll()
                }
            }
        }
        mainRecyclerView.addOnScrollListener(pagingScrollListener)
        nameRecyclerView.addOnScrollListener(pagingScrollListener)
    }

    private fun setupHeaderSpaceView() {
        headerSpaceView = viewCreator.getHeaderSpaceView()
        headerSpaceView.setOnClickListener {
            timeHeaderClickListener.onClickTimeHeader()
        }
        this.addView(headerSpaceView)
    }

    private fun setupTopSpaceView() {
        topSpaceView = viewCreator.getTopSpaceView()
        this.addView(topSpaceView)
    }

    private fun setupObserveScroll() {
        scrollListenerFromMainRv = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                saveScrollY += dy
                nameRecyclerView.scrollBy(0, dy)
            }
        }
        mainRecyclerView.addOnScrollListener(scrollListenerFromMainRv)
    }

    private fun setupDisableScroll(){
        nameRecyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return when (e.action) {
                    MotionEvent.ACTION_DOWN -> false
                    else -> true
                }
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                //onTouchEvent
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                //onRequestDisallowInterceptTouchEvent
            }
        })
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupSnappingEffect(view: HorizontalScrollView) {
        view.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                    val scrollX = v.scrollX
                    val featureWidth = perTopHeaderWidth
                    mActiveFeature = (scrollX + featureWidth / 2) / featureWidth
                    val scrollTo = mActiveFeature * featureWidth
                    view.smoothScrollTo(scrollTo, 0)
                    horizontalScrollState = SCROLL_ATLEAST_ONCE
                    return true
                }
                return false
            }
        })
    }

    private fun setupInfiniteHorizontalScroll(v: HorizontalScrollView) {
        val loadManager = LoadManager(loadingDialog,scrollListenerFromMainRv)
        v.viewTreeObserver.addOnScrollChangedListener {
            if (horizontalScrollState == SCROLL_ATLEAST_ONCE) {
                if (viewType == ViewType.WEEK && v.scrollX == START_OF_SCROLL && !isLoadPriorSchedule) {
                    isLoadPriorSchedule = true
                    loadManager.loadPriorWeekSchedule(
                        mainRecyclerView,
                        coverMainRvHorizontalView,
                        mainAdapter,
                        expandedPositionList,
                        saveScrollY
                    )
                    setFlagWithDelay(1)
                    dateChangeListener.OnDateChange(getDays(currentTimeForWeek, 7))
                    currentTimeForDay = getDays(currentTimeForDay, -7)
                } else if (viewType == ViewType.WEEK && v.scrollX > perTopHeaderWidth * 10 && !isLoadLaterSchedule) {
                    isLoadLaterSchedule = true
                    loadManager.loadLaterWeekSchedule(
                        mainRecyclerView,
                        coverMainRvHorizontalView,
                        mainAdapter,
                        expandedPositionList,
                        saveScrollY
                    )
                    setFlagWithDelay(2)
                    dateChangeListener.OnDateChange(getDays(currentTimeForWeek, 4))
                    currentTimeForDay = getDays(currentTimeForDay, 3)
                } else if (viewType == ViewType.DAY && v.scrollX == START_OF_SCROLL && !isLoadPriorSchedule) {
                    isLoadPriorSchedule = true
                    loadManager.loadPriorDaySchedule(
                        mainRecyclerView,
                        coverMainRvHorizontalView,
                        mainAdapter,
                        expandedPositionList,
                        saveScrollY
                    )
                    setFlagWithDelay(1)
                    dateChangeListener.OnDateChange(currentTimeForDay)
                } else if (viewType == ViewType.DAY && v.scrollX > perTopHeaderWidth * 20 && !isLoadLaterSchedule) {
                    isLoadLaterSchedule = true
                    loadManager.loadLaterDaySchedule(
                        mainRecyclerView,
                        coverMainRvHorizontalView,
                        mainAdapter,
                        expandedPositionList,
                        saveScrollY
                    )
                    setFlagWithDelay(2)
                    dateChangeListener.OnDateChange(currentTimeForDay)
                }
            }
        }
    }

    private fun setupDragListener() {
        dragManager = DragManager(
            this,
            coverMainRvHorizontalView,
            mainRecyclerView,
            mainAdapter,
            expandedPositionList,
            scheduleDropListener,
            scrollListenerFromMainRv
        )
        dragManager.setupDragListener()
    }

    private fun setFlagWithDelay(type: Int) {
        Handler(Looper.getMainLooper()).postDelayed({
            when (type) {
                1 -> isLoadPriorSchedule = false
                2 -> isLoadLaterSchedule = false
            }
        }, 300)
    }

    private fun resetRv() {
        expandedPositionList.clear()
        expandedPositionList.add(0)
        saveScrollY = 0
        currentTimeForWeek = getDays(currentTimeForDay, -7)
        mainRecyclerView.removeAllViewsInLayout()
        nameRecyclerView.removeAllViewsInLayout()
        mainRecyclerView.adapter = mainAdapter
        nameRecyclerView.adapter = nameAdapter
        nameRecyclerView.scrollToPosition(0)
        Handler(Looper.getMainLooper()).postDelayed({
            nameRecyclerView.scrollToPosition(0)
            mainRecyclerView.scrollToPosition(0)
        }, 100)
        when (viewType) {
            ViewType.DAY -> coverMainRvHorizontalView.scrollTo(perTopHeaderWidth * 10, 0)
            ViewType.WEEK -> coverMainRvHorizontalView.scrollTo(perTopHeaderWidth * 7, 0)
        }
    }

    private fun getSectionCallback(): StickyHeaderItemDecoration.SectionCallback {
        return object : StickyHeaderItemDecoration.SectionCallback {
            override fun getHeaderLayoutView(list: RecyclerView, position: Int): View {
                return mainAdapter.getHeaderView(list)
            }
        }
    }

    fun setOnClickScheduleListener(listener: OnClickScheduleListener) {
        scheduleClickListener = listener
        mainAdapter.setOnClickScheduleListener(scheduleClickListener)
    }

    fun setOnClickTimeHeaderListener(listener: OnClickTimeHeaderListener) {
        timeHeaderClickListener = listener

    }

    fun setOnClickNameHeaderListener(listener: OnClickNameHeaderListener) {
        nameHeaderClickListener = listener
        nameAdapter.setOnClickNameHeaderListener(nameHeaderClickListener)
    }

    fun setOnPageChangeListener(listener: OnPageChangeListener) {
        pageChangeListener = listener
    }

    fun setOnDateChangeListener(listener: OnDateChangeListener) {
        dateChangeListener = listener
    }

    fun setOnScheduleDropListener(listener: OnScheduleDropListener) {
        dragManager.setScheduleDropListener(listener)
    }

    fun setDate(time: Time) {
        currentTimeForDay = time
        resetRv()
    }

    fun submitUserList(list: ArrayList<User>) {
        mainAdapter.submitList(list.toMutableList())
        nameAdapter.submitList(list.map { it.name }.toMutableList())
        mainRecyclerView.setItemViewCacheSize(mainAdapter.currentList.size + 10)
        nameRecyclerView.setItemViewCacheSize(nameAdapter.currentList.size + 10)
        Handler(Looper.getMainLooper()).postDelayed(
            { resetRv() }, 100
        )
    }

    fun getUserList()= mainAdapter.currentList

    fun setViewType(value: Int) {
        when(value){
            0 -> viewType = ViewType.DAY
            1 -> viewType = ViewType.WEEK
        }
        resetRv()
    }

    fun resetCurrentPage() {
        horizontalScrollState = NOT_SCROLLED
        pagingScrollListener.resetState()
    }

    fun setList(list: MutableList<User>) {
        mainAdapter.setList(list)
        nameAdapter.setList(list.map { it.name }.toMutableList())
        mainRecyclerView.setItemViewCacheSize(mainAdapter.currentList.size + 10)
        nameRecyclerView.setItemViewCacheSize(nameAdapter.currentList.size + 10)
    }

    fun resetScroll() {
        Handler(Looper.getMainLooper()).postDelayed({
            mainRecyclerView.scrollToPosition(0)
            when (viewType) {
                ViewType.DAY -> coverMainRvHorizontalView.scrollTo(perTopHeaderWidth * 10, 0)
                ViewType.WEEK -> coverMainRvHorizontalView.scrollTo(perTopHeaderWidth * 7, 0)
            }
        }, 100)
    }
}