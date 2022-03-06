package com.example.memberscheduledev.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.library.model.Schedule
import com.example.library.model.User
import com.example.memberscheduledev.data.source.SchedulesDataSource
import com.example.memberscheduledev.data.source.SchedulesRepository
import com.example.memberscheduledev.util.Tools.convertDate
import com.example.memberscheduledev.util.Tools.notifyObserver
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeViewModel(private val scheduleRepository: SchedulesRepository) : ViewModel() {

    private val _currentDate = MutableLiveData<String>()
    val currentDate: LiveData<String>
        get() = _currentDate

    private val _viewType = MutableLiveData<Int>()
    val viewType: LiveData<Int>
        get() = _viewType

    private val _userList = MutableLiveData<MutableList<User>>(ArrayList())
    val userList: LiveData<MutableList<User>>
        get() = _userList

    private val _pagingUserList = MutableLiveData<MutableList<User>>(ArrayList())
    val pagingUserList: LiveData<MutableList<User>>
        get() = _pagingUserList


    private val _calendar = MutableLiveData(Calendar.getInstance())
    val calendar: LiveData<Calendar>
        get() = _calendar

    init {
        _currentDate.value = "${calendar.value!!.get(Calendar.YEAR)}" +
                ".${calendar.value!!.get(Calendar.MONTH)}" +
                ".${calendar.value!!.get(Calendar.DAY_OF_MONTH)}"

    }

    fun loadUserList() {
        var dbUserList = ArrayList<com.example.memberscheduledev.data.User>()
        viewModelScope.launch {
            scheduleRepository.getUserList(object : SchedulesDataSource.GetUserListCallback {
                override fun onUserListLoaded(userList: List<com.example.memberscheduledev.data.User>) {
                    dbUserList =
                        userList as ArrayList<com.example.memberscheduledev.data.User>
                }

                override fun onDataNotAvailable() {
                    Log.d("TAG", "dbUserList not available  ")
                }
            })
            val saveUserList = ArrayList<User>()
            for (item in dbUserList) {
                var dbScheduleList =
                    ArrayList<com.example.memberscheduledev.data.Schedule>()
                scheduleRepository.getScheduleList(item.id,
                    object : SchedulesDataSource.LoadSchedulesCallback {
                        override fun onSchedulesLoaded(scheduleList: List<com.example.memberscheduledev.data.Schedule>) {
                            dbScheduleList =
                                scheduleList as ArrayList<com.example.memberscheduledev.data.Schedule>

                        }

                        override fun onDataNotAvailable() {
                            Log.d("TAG", "onDataNotAvailable: getScheduleList() ")
                        }
                    })
                val scheduleList = ArrayList<Schedule>()
                for (i in dbScheduleList) {
                    val schedule = i.toScheduleTime()
                    scheduleList.add(schedule)
                }
                val user = User(item.id, item.name, scheduleList)
                saveUserList.add(user)
            }
            _userList.value = saveUserList
            _userList.notifyObserver()
        }
    }

    fun updateCurrentDateText(input: String) {
        _currentDate.value = input
    }


    fun updateViewType(input: Int) {
        _viewType.value = input
    }

    fun updateCalendar(cal: Calendar) {
        _calendar.value = cal
    }

    fun loadUserListPaging(page: Int) {
        var dbUserList = ArrayList<com.example.memberscheduledev.data.User>()
        viewModelScope.launch {
            scheduleRepository.getUserListPaging(object : SchedulesDataSource.GetUserListCallback {
                override fun onUserListLoaded(userList: List<com.example.memberscheduledev.data.User>) {
                    dbUserList =
                        userList as ArrayList<com.example.memberscheduledev.data.User>
                }

                override fun onDataNotAvailable() {
                    Log.d("TAG", "dbUserList not available  ")
                }
            }, page)
            val saveUserList = ArrayList<User>()
            for (item in dbUserList) {
                var dbScheduleList =
                    ArrayList<com.example.memberscheduledev.data.Schedule>()
                scheduleRepository.getScheduleList(item.id,
                    object : SchedulesDataSource.LoadSchedulesCallback {
                        override fun onSchedulesLoaded(scheduleList: List<com.example.memberscheduledev.data.Schedule>) {
                            dbScheduleList =
                                scheduleList as ArrayList<com.example.memberscheduledev.data.Schedule>

                        }

                        override fun onDataNotAvailable() {
                            Log.d("TAG", "onDataNotAvailable: getScheduleList() ")
                        }
                    })
                val scheduleList = ArrayList<Schedule>()
                for (i in dbScheduleList) {
                    val schedule = i.toScheduleTime()
                    scheduleList.add(schedule)
                }
                val user = User(item.id, item.name, scheduleList)
                saveUserList.add(user)
            }
            _pagingUserList.value = saveUserList
        }
    }

    fun modifySchedule(changeUserId: String, schedule: Schedule) {
        viewModelScope.launch {
            lateinit var dbSchedule: com.example.memberscheduledev.data.Schedule
            scheduleRepository.getSchedule(schedule.id,
                object : SchedulesDataSource.GetScheduleCallback {
                    override fun onScheduleLoaded(schedule: com.example.memberscheduledev.data.Schedule) {
                        dbSchedule = schedule
                    }

                    override fun onDataNotAvailable() {
                        //cannot find schedule
                    }
                })
            var startDate =
                "${schedule.startDate.year}.${schedule.startDate.month}.${schedule.startDate.day}.${schedule.startDate.hour}.${schedule.startDate.minute}"
            startDate = convertDate(startDate)
            var endDate =
                "${schedule.endDate.year}.${schedule.endDate.month}.${schedule.endDate.day}.${schedule.endDate.hour}.${schedule.endDate.minute}"
            endDate = convertDate(endDate)
            dbSchedule.userId = changeUserId
            dbSchedule.startDate = startDate
            dbSchedule.endDate = endDate
            scheduleRepository.modifySchedule(dbSchedule)
        }
    }

}
