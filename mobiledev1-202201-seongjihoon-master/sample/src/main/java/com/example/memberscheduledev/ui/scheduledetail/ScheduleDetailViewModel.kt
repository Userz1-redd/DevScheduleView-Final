package com.example.memberscheduledev.ui.scheduledetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memberscheduledev.data.Schedule
import com.example.memberscheduledev.data.source.SchedulesDataSource
import com.example.memberscheduledev.data.source.SchedulesRepository
import kotlinx.coroutines.launch

class ScheduleDetailViewModel(private val scheduleRepository: SchedulesRepository) : ViewModel() {

    private val _schedule = MutableLiveData<Schedule>()
    val schedule: LiveData<Schedule>
        get() = _schedule

    private val _userNameString = MutableLiveData("")
    val userNameString: LiveData<String>
        get() = _userNameString

    private val _scheduleNameString = MutableLiveData("")
    val scheduleNameString: LiveData<String>
        get() = _scheduleNameString

    private val _allDayChecked = MutableLiveData(false)
    val allDayChecked: LiveData<Boolean>
        get() = _allDayChecked

    private val _startDateString = MutableLiveData<String>()
    val startDateString: LiveData<String>
        get() = _startDateString

    private val _startTimeString = MutableLiveData<String>()
    val startTimeString: LiveData<String>
        get() = _startTimeString

    private val _endDateString = MutableLiveData<String>()
    val endDateString: LiveData<String>
        get() = _endDateString

    private val _endTimeString = MutableLiveData<String>()
    val endTimeString: LiveData<String>
        get() = _endTimeString

    fun getScheduleInfo(scheduleId: String) {
        viewModelScope.launch {
            scheduleRepository.getSchedule(
                scheduleId,
                object : SchedulesDataSource.GetScheduleCallback {
                    override fun onScheduleLoaded(schedule: Schedule) {
                        _schedule.value = schedule
                        val startDate = schedule.startDate.split(".")
                        val endDate = schedule.endDate.split(".")
                        _scheduleNameString.value = schedule.name
                        _startDateString.value = "${startDate[0]}.${startDate[1]}.${startDate[2]}"
                        _startTimeString.value = "${startDate[3]}시 ${startDate[4]}분"
                        _endDateString.value = "${endDate[0]}.${endDate[1]}.${endDate[2]}"
                        _endTimeString.value = "${endDate[3]}시 ${endDate[4]}분"
                        _allDayChecked.value = startTimeString.value == "0시 0분" && endTimeString.value == "23시 59분"
                    }

                    override fun onDataNotAvailable() {
                        Log.d("TAG", "onDataNotAvailable: cannot find schedule")
                    }

                })
        }
    }

    fun deleteSchedule() {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule.value!!)
        }
    }

    fun updateUserName(value: String) {
        _userNameString.value = value
    }
}