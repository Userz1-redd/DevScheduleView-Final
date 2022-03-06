package com.example.memberscheduledev.ui.scheduleadd

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.memberscheduledev.R
import com.example.memberscheduledev.ViewModelFactory
import com.example.memberscheduledev.data.source.SchedulesRepository
import com.example.memberscheduledev.data.source.local.SchedulesDatabase
import com.example.memberscheduledev.data.source.local.SchedulesLocalDataSource
import com.example.memberscheduledev.databinding.ActivityScheduleAddBinding
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

import java.util.*

class ScheduleAddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleAddBinding
    private lateinit var startDatePickerDialog: DatePickerDialog
    private lateinit var endDatePickerDialog: DatePickerDialog
    private lateinit var startTimePickerDialog: TimePickerDialog
    private lateinit var endTimePickerDialog: TimePickerDialog
    private var compositeDisposable = CompositeDisposable()
    private val database by lazy {
        SchedulesDatabase.getDatabase(this)
    }
    private val repository by lazy {
        SchedulesRepository.getInstance(SchedulesLocalDataSource(database.schedulesDao()))
    }

    private val viewModel: ScheduleAddViewModel by viewModels {
        ViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_add)
        setupCalendarAndPicker()

        setupTextChanges()

        setupOnClickBtn()

        setupObserveData()

    }

    private fun setupCalendarAndPicker() {
        startDatePickerDialog = DatePickerDialog(
            this, null, 0, 0, 0
        )
        startTimePickerDialog = TimePickerDialog(
            this, null, 0, 0, true
        )
        endDatePickerDialog = DatePickerDialog(
            this, null, 0, 0, 0
        )
        endTimePickerDialog = TimePickerDialog(
            this, null, 0, 0, true
        )

    }

    private fun setupTextChanges() {
        compositeDisposable.apply {
            add(binding.usernameText.textChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.updateUserNameString(it.toString()) }
            )
            add(binding.scheduleTitleText.textChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.updateScheduleNameString(it.toString()) }
            )
        }
    }

    private fun setupOnClickBtn() {

        binding.checkAllDayBtn.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateAllDayBtn(isChecked)
        }

        binding.saveText.setOnClickListener {
            if (!checkText()) {
                SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("오류")
                    .setContentText("이름을 확인하세요")
                    .setConfirmText("확인")
                    .show()
            } else {
                when (checkDateAndTime()) {
                    1 -> {
                        viewModel.insertSchedule()
                        SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("성공")
                            .setContentText("일정을 추가했습니다.")
                            .setConfirmText("확인")
                            .setConfirmClickListener {
                                finish()
                            }.show()
                    }
                    else -> {
                        SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("오류")
                            .setContentText("시작과 종료일을 확인하세요.")
                            .setConfirmText("확인")
                            .show()
                    }
                }
            }
        }

        binding.startdateText.setOnClickListener {
            val startCal = viewModel.startCalendar.value!!
            val startDateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    startCal.set(Calendar.YEAR, year)
                    startCal.set(Calendar.MONTH, month)
                    startCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    viewModel.updateStartCalendar(startCal)
                }
            startDatePickerDialog = DatePickerDialog(
                this, startDateSetListener, startCal.get(Calendar.YEAR), startCal.get(
                    Calendar.MONTH
                ), startCal.get(Calendar.DAY_OF_MONTH)
            )
            startDatePickerDialog.show()
        }
        binding.starttimeText.setOnClickListener {
            val startCal = viewModel.startCalendar.value!!
            val startTimeSetListener =
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    startCal.set(Calendar.HOUR_OF_DAY, hour)
                    startCal.set(Calendar.MINUTE, minute)
                    viewModel.updateStartCalendar(startCal)
                }
            startTimePickerDialog = TimePickerDialog(
                this, startTimeSetListener, startCal.get(Calendar.HOUR_OF_DAY), startCal.get(
                    Calendar.MINUTE
                ), true
            )
            startTimePickerDialog.show()
        }

        binding.enddateText.setOnClickListener {
            val endCal = viewModel.endCalendar.value!!
            val endDateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    endCal.set(Calendar.YEAR, year)
                    endCal.set(Calendar.MONTH, month)
                    endCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    viewModel.updateEndCalendar(endCal)
                }
            endDatePickerDialog = DatePickerDialog(
                this, endDateSetListener, endCal.get(Calendar.YEAR), endCal.get(
                    Calendar.MONTH
                ), endCal.get(Calendar.DAY_OF_MONTH)
            )
            endDatePickerDialog.show()
        }
        binding.endtimeText.setOnClickListener {
            val endCal = viewModel.endCalendar.value!!
            val endTimeSetListener =
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    endCal.set(Calendar.HOUR_OF_DAY, hour)
                    endCal.set(Calendar.MINUTE, minute)
                    viewModel.updateEndCalendar(endCal)
                }
            endTimePickerDialog = TimePickerDialog(
                this, endTimeSetListener, endCal.get(Calendar.HOUR_OF_DAY), endCal.get(
                    Calendar.MINUTE
                ), true
            )
            endTimePickerDialog.show()
        }
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupObserveData() {
        viewModel.startCalendar.observe(this, {
            startDatePickerDialog.updateDate(
                it.get(Calendar.YEAR),
                it.get(Calendar.MONTH),
                it.get(Calendar.DAY_OF_MONTH)
            )
            startTimePickerDialog.updateTime(
                it.get(Calendar.HOUR_OF_DAY),
                it.get(Calendar.MINUTE)
            )
            viewModel.updateStartDateAndTimeString()
        })
        viewModel.endCalendar.observe(this, {
            endDatePickerDialog.updateDate(
                it.get(Calendar.YEAR),
                it.get(Calendar.MONTH),
                it.get(Calendar.DAY_OF_MONTH)
            )
            endTimePickerDialog.updateTime(it.get(Calendar.HOUR_OF_DAY), it.get(Calendar.MINUTE))
            viewModel.updateEndDateAndTimeString()

        })
        viewModel.startDateString.observe(this, {
            binding.startdateText.text = it
        })
        viewModel.startTimeString.observe(this, {
            val splitString = it.split(".")
            binding.starttimeText.text = "${splitString[0]}시 ${splitString[1]}분"
        })
        viewModel.endDateString.observe(this, {
            binding.enddateText.text = it
        })
        viewModel.endTimeString.observe(this, {
            val splitString = it.split(".")
            binding.endtimeText.text = "${splitString[0]}시 ${splitString[1]}분"
        })


        viewModel.allDayChecked.observe(this, {
            if (it) {
                val sCalendar = viewModel.startCalendar.value!!
                sCalendar.apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                }
                val eCalendar = viewModel.endCalendar.value!!
                eCalendar.apply {
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                }
                viewModel.updateStartCalendar(sCalendar)
                viewModel.updateEndCalendar(eCalendar)
            }
            binding.starttimeText.isClickable = !it
            binding.endtimeText.isClickable = !it
        })
    }

    private fun checkText(): Boolean {
        return viewModel.userNameString.value!!.isNotEmpty()
                && viewModel.scheduleNameString.value!!.isNotEmpty()
    }

    private fun checkDateAndTime() =
        viewModel.endCalendar.value!!.compareTo(viewModel.startCalendar.value!!)

    override fun onDestroy() {
        compositeDisposable.clear()
        startDatePickerDialog.dismiss()
        endDatePickerDialog.dismiss()
        startTimePickerDialog.dismiss()
        endTimePickerDialog.dismiss()
        super.onDestroy()
    }

}