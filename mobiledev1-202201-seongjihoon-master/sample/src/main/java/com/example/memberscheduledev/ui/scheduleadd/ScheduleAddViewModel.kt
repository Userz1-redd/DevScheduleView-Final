package com.example.memberscheduledev.ui.scheduleadd

import androidx.lifecycle.*
import com.example.memberscheduledev.data.Schedule
import com.example.memberscheduledev.data.source.SchedulesRepository
import com.example.memberscheduledev.util.Tools.convertDate
import kotlinx.coroutines.launch
import java.util.*

class ScheduleAddViewModel(private val scheduleRepository: SchedulesRepository) : ViewModel() {

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

    private val _startCalendar = MutableLiveData(Calendar.getInstance())
    val startCalendar: LiveData<Calendar>
        get() = _startCalendar

    private val _endCalendar = MutableLiveData(Calendar.getInstance())
    val endCalendar: LiveData<Calendar>
        get() = _endCalendar


    init {
        _endCalendar.value!!.set(
            Calendar.HOUR,
            (startCalendar.value!!.get(Calendar.HOUR).plus(1).rem(24))
        )
        _startDateString.value = "${_startCalendar.value!!.get(Calendar.YEAR)}" +
                ".${_startCalendar.value!!.get(Calendar.MONTH) + 1}" +
                ".${_startCalendar.value!!.get(Calendar.DAY_OF_MONTH)}"
        _startTimeString.value = "${_startCalendar.value!!.get(Calendar.HOUR_OF_DAY)}" +
                ".${_startCalendar.value!!.get(Calendar.MINUTE)}"
        _endDateString.value = "${_endCalendar.value!!.get(Calendar.YEAR)}" +
                ".${_endCalendar.value!!.get(Calendar.MONTH) + 1}" +
                ".${_endCalendar.value!!.get(Calendar.DAY_OF_MONTH)}"
        _endTimeString.value = "${_endCalendar.value!!.get(Calendar.HOUR_OF_DAY)}" +
                ".${_endCalendar.value!!.get(Calendar.MINUTE)}"
    }

    fun insertSchedule() {
        viewModelScope.launch {
            scheduleRepository.addSchedule(
                userNameString.value!!, Schedule(
                    scheduleNameString.value!!,
                    convertDate("${startDateString.value}.${startTimeString.value}"),
                    convertDate("${endDateString.value}.${endTimeString.value}")
                )
            )
        }
    }

    fun updateStartCalendar(startCal: Calendar) {
        _startCalendar.value = startCal
    }

    fun updateEndCalendar(endCal: Calendar) {
        _endCalendar.value = endCal
    }

    fun updateStartDateAndTimeString() {
        _startDateString.value = "${_startCalendar.value!!.get(Calendar.YEAR)}" +
                ".${_startCalendar.value!!.get(Calendar.MONTH) + 1}" +
                ".${_startCalendar.value!!.get(Calendar.DAY_OF_MONTH)}"
        _startTimeString.value = "${_startCalendar.value!!.get(Calendar.HOUR_OF_DAY)}" +
                ".${_startCalendar.value!!.get(Calendar.MINUTE)}"
    }

    fun updateEndDateAndTimeString() {
        _endDateString.value = "${_endCalendar.value!!.get(Calendar.YEAR)}" +
                ".${_endCalendar.value!!.get(Calendar.MONTH) + 1}" +
                ".${_endCalendar.value!!.get(Calendar.DAY_OF_MONTH)}"
        _endTimeString.value = "${_endCalendar.value!!.get(Calendar.HOUR_OF_DAY)}" +
                ".${_endCalendar.value!!.get(Calendar.MINUTE)}"
    }

    fun updateUserNameString(value: String) {
        _userNameString.value = value
    }

    fun updateScheduleNameString(value: String) {
        _scheduleNameString.value = value
    }

    fun updateAllDayBtn(b: Boolean) {
        _allDayChecked.value = b
    }
}

