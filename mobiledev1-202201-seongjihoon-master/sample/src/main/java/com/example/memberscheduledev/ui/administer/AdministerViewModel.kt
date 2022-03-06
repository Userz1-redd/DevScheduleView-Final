package com.example.memberscheduledev.ui.administer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memberscheduledev.data.Schedule
import com.example.memberscheduledev.data.source.SchedulesRepository
import com.example.memberscheduledev.util.Tools.convertDate
import kotlinx.coroutines.launch
import java.util.*

class AdministerViewModel(private val scheduleRepository: SchedulesRepository) : ViewModel() {
    private val _numberOfUser = MutableLiveData("0")
    val numberOfUser: LiveData<String>
        get() = _numberOfUser

    private val _numberOfSchedule = MutableLiveData("0")
    val numberOfSchedule: LiveData<String>
        get() = _numberOfSchedule

    private val _startDateString = MutableLiveData<String>()
    val startDateString: LiveData<String>
        get() = _startDateString

    private val _startCalendar = MutableLiveData(Calendar.getInstance())
    val startCalendar: LiveData<Calendar>
        get() = _startCalendar

    init {
        _startDateString.value = "${_startCalendar.value!!.get(Calendar.YEAR)}" +
                ".${_startCalendar.value!!.get(Calendar.MONTH) + 1}" +
                ".${_startCalendar.value!!.get(Calendar.DAY_OF_MONTH)}"
    }

    fun updateStartDateString() {
        _startDateString.value = "${_startCalendar.value!!.get(Calendar.YEAR)}" +
                ".${_startCalendar.value!!.get(Calendar.MONTH) + 1}" +
                ".${_startCalendar.value!!.get(Calendar.DAY_OF_MONTH)}"
    }

    fun addNormalSchedule() {
        viewModelScope.launch {
            val startDate = "${_startCalendar.value!!.get(Calendar.YEAR)}" +
                    ".${_startCalendar.value!!.get(Calendar.MONTH) + 1}" +
                    ".${_startCalendar.value!!.get(Calendar.DAY_OF_MONTH)}" +
                    ".02.00"
            val endDate = "${_startCalendar.value!!.get(Calendar.YEAR)}" +
                    ".${_startCalendar.value!!.get(Calendar.MONTH) + 1}" +
                    ".${_startCalendar.value!!.get(Calendar.DAY_OF_MONTH)}" +
                    ".03.00"
            scheduleRepository.addSchedule(
                "사용자1", Schedule(
                    "1번테스트", convertDate(startDate),
                    convertDate(endDate)
                )
            )
        }
    }

    fun addThreeDaysSchedule() {
        viewModelScope.launch {
            val endCal = Calendar.getInstance()
            endCal.set(
                _startCalendar.value!!.get(Calendar.YEAR),
                _startCalendar.value!!.get(Calendar.MONTH),
                _startCalendar.value!!.get(Calendar.DAY_OF_MONTH)
            )
            endCal.add(Calendar.DAY_OF_MONTH, 2)
            val startDate = "${_startCalendar.value!!.get(Calendar.YEAR)}" +
                    ".${_startCalendar.value!!.get(Calendar.MONTH) + 1}" +
                    ".${_startCalendar.value!!.get(Calendar.DAY_OF_MONTH)}" +
                    ".20.00"
            val endDate = "${endCal.get(Calendar.YEAR)}" +
                    ".${endCal.get(Calendar.MONTH) + 1}" +
                    ".${endCal.get(Calendar.DAY_OF_MONTH)}" +
                    ".03.00"
            scheduleRepository.addSchedule(
                "사용자2", Schedule(
                    "2번테스트", convertDate(startDate),
                    convertDate(endDate)
                )
            )
        }
    }

    fun addThreeScheduleInDay() {
        viewModelScope.launch {
            for (i in 1..3) {
                val startDate = "${_startCalendar.value!!.get(Calendar.YEAR)}" +
                        ".${_startCalendar.value!!.get(Calendar.MONTH) + 1}" +
                        ".${_startCalendar.value!!.get(Calendar.DAY_OF_MONTH)}" +
                        ".0${i * 2}.00"
                val endDate = "${_startCalendar.value!!.get(Calendar.YEAR)}" +
                        ".${_startCalendar.value!!.get(Calendar.MONTH) + 1}" +
                        ".${_startCalendar.value!!.get(Calendar.DAY_OF_MONTH)}" +
                        ".0${i * 2 + 1}.00"
                scheduleRepository.addSchedule(
                    "사용자3", Schedule(
                        "3번테스트", convertDate(startDate),
                        convertDate(endDate)
                    )
                )
            }
        }
    }

    fun addThreeScheduleInDayDup() {
        viewModelScope.launch {
            for (i in 1..3) {
                val startDate = "${_startCalendar.value!!.get(Calendar.YEAR)}" +
                        ".${_startCalendar.value!!.get(Calendar.MONTH) + 1}" +
                        ".${_startCalendar.value!!.get(Calendar.DAY_OF_MONTH)}" +
                        ".0${i}.00"
                val endDate = "${_startCalendar.value!!.get(Calendar.YEAR)}" +
                        ".${_startCalendar.value!!.get(Calendar.MONTH) + 1}" +
                        ".${_startCalendar.value!!.get(Calendar.DAY_OF_MONTH)}" +
                        ".0${i + 4}.00"
                scheduleRepository.addSchedule(
                    "사용자4", Schedule(
                        "4번테스트", convertDate(startDate),
                        convertDate(endDate)
                    )
                )
            }
        }
    }

    fun addRandomSchedule() {
        viewModelScope.launch {
            for (i in 1.._numberOfUser.value!!.toInt()) {
                for (j in 1.._numberOfSchedule.value!!.toInt()) {
                    val startCal = Calendar.getInstance()
                    startCal.set(
                        _startCalendar.value!!.get(Calendar.YEAR),
                        _startCalendar.value!!.get(Calendar.MONTH),
                        _startCalendar.value!!.get(Calendar.DAY_OF_MONTH)
                    )
                    val startPlusDay = Random().nextInt(4)
                    startCal.add(Calendar.DAY_OF_MONTH, startPlusDay)
                    val endCal = Calendar.getInstance()
                    endCal.set(
                        startCal.get(Calendar.YEAR),
                        startCal.get(Calendar.MONTH),
                        startCal.get(Calendar.DAY_OF_MONTH) + 1
                    )
                    endCal.add(Calendar.DAY_OF_MONTH, Random().nextInt(4))
                    val startDate =
                        "${startCal.get(Calendar.YEAR)}.${startCal.get(Calendar.MONTH) + 1}" +
                                ".${startCal.get(Calendar.DAY_OF_MONTH)}.${Random().nextInt(24)}.${
                                    Random().nextInt(
                                        60
                                    )
                                }"
                    val endDate = "${endCal.get(Calendar.YEAR)}.${endCal.get(Calendar.MONTH) + 1}" +
                            ".${endCal.get(Calendar.DAY_OF_MONTH)}.${Random().nextInt(24)}.${
                                Random().nextInt(
                                    60
                                )
                            }"
                    scheduleRepository.addSchedule(
                        i.toString(), Schedule(
                            "일정${j}", convertDate(startDate),
                            convertDate(endDate)
                        )
                    )
                }
            }
        }
    }

    fun addRandomScheduleInWeek() {
        viewModelScope.launch {
            for (i in 1.._numberOfUser.value!!.toInt()) {
                for (j in 1.._numberOfSchedule.value!!.toInt()) {
                    val startCal = Calendar.getInstance()
                    startCal.set(
                        _startCalendar.value!!.get(Calendar.YEAR),
                        _startCalendar.value!!.get(Calendar.MONTH),
                        _startCalendar.value!!.get(Calendar.DAY_OF_MONTH)
                    )
                    val startPlusDay = Random().nextInt(7)
                    startCal.add(Calendar.DAY_OF_MONTH, startPlusDay)
                    val startTime = Random().nextInt(19)
                    val startDate =
                        "${startCal.get(Calendar.YEAR)}.${startCal.get(Calendar.MONTH) + 1}" +
                                ".${startCal.get(Calendar.DAY_OF_MONTH)}.${startTime}.${
                                    Random().nextInt(
                                        60
                                    )
                                }"
                    val endDate =
                        "${startCal.get(Calendar.YEAR)}.${startCal.get(Calendar.MONTH) + 1}" +
                                ".${startCal.get(Calendar.DAY_OF_MONTH)}.${
                                    startTime + 1 + Random().nextInt(
                                        4
                                    )
                                }.${
                                    Random().nextInt(
                                        60
                                    )
                                }"
                    scheduleRepository.addSchedule(
                        i.toString(), Schedule(
                            "일정${j}", convertDate(startDate),
                            convertDate(endDate)
                        )
                    )
                }
            }
        }
    }


    fun addRandomDupSchedule() {
        viewModelScope.launch {
            for (i in 1.._numberOfUser.value!!.toInt()) {
                for (j in 1.._numberOfSchedule.value!!.toInt()) {
                    val startTime = Random().nextInt(19)
                    val startDate = "${_startDateString.value}.${startTime}.${Random().nextInt(60)}"
                    val endDate =
                        "${_startDateString.value}.${startTime + 1 + Random().nextInt(5)}.${
                            Random().nextInt(60)
                        }"
                    scheduleRepository.addSchedule(
                        i.toString(), Schedule(
                            "일정${j}", convertDate(startDate),
                            convertDate(endDate)
                        )
                    )
                }
            }
        }
    }

    fun updateStartCalendar(startCal: Calendar) {
        _startCalendar.value = startCal
    }

    fun updateNumberOfUser(value: String) {
        _numberOfUser.value = value
    }

    fun updateNumberOfSchedule(value: String) {
        _numberOfSchedule.value = value
    }
}