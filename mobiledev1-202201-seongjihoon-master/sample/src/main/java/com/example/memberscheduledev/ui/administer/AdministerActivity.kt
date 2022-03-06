package com.example.memberscheduledev.ui.administer

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.memberscheduledev.R
import com.example.memberscheduledev.ViewModelFactory
import com.example.memberscheduledev.data.source.SchedulesRepository
import com.example.memberscheduledev.data.source.local.SchedulesDatabase
import com.example.memberscheduledev.data.source.local.SchedulesLocalDataSource
import com.example.memberscheduledev.databinding.ActivityAdministerBinding
import com.example.memberscheduledev.util.LoadingDialog
import com.jakewharton.rxbinding4.widget.textChanges
import com.shashank.sony.fancytoastlib.FancyToast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AdministerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdministerBinding
    private lateinit var startDatePickerDialog: DatePickerDialog
    private lateinit var endDatePickerDialog: DatePickerDialog
    private var compositeDisposable = CompositeDisposable()
    private val database by lazy {
        SchedulesDatabase.getDatabase(this)
    }
    private val repository by lazy {
        SchedulesRepository.getInstance(SchedulesLocalDataSource(database.schedulesDao()))
    }

    private val viewModel: AdministerViewModel by viewModels {
        ViewModelFactory(repository)
    }

    @DelicateCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_administer)
        setupPicker()

        setupTextChanges()

        setupObserveData()

        setupOnClickBtn()
    }

    private fun setupPicker() {
        startDatePickerDialog = DatePickerDialog(
            this, null, 0, 0, 0
        )
        endDatePickerDialog = DatePickerDialog(
            this, null, 0, 0, 0
        )
    }


    private fun setupTextChanges() {
        compositeDisposable.apply {
            add(binding.usernumberText.textChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.updateNumberOfUser(it.toString()) }
            )
            add(binding.schedulenumberText.textChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.updateNumberOfSchedule(it.toString()) }
            )
        }
    }

    @DelicateCoroutinesApi
    private fun setupOnClickBtn() {
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

        binding.addNormalSchedule.setOnClickListener {
            viewModel.addNormalSchedule()
            FancyToast.makeText(
                applicationContext,
                "1번케이스 추가",
                FancyToast.LENGTH_SHORT,
                FancyToast.SUCCESS,
                true
            )
                .show()
        }

        binding.addOverThreedaysSchedule.setOnClickListener {
            viewModel.addThreeDaysSchedule()
            FancyToast.makeText(
                applicationContext,
                "2번케이스 추가",
                FancyToast.LENGTH_SHORT,
                FancyToast.SUCCESS,
                true
            )
                .show()
        }
        binding.addThreeScheduleInDay.setOnClickListener {
            viewModel.addThreeScheduleInDay()
            FancyToast.makeText(
                applicationContext,
                "3번케이스 추가",
                FancyToast.LENGTH_SHORT,
                FancyToast.SUCCESS,
                true
            )
                .show()
        }
        binding.addThreeScheduleInDayDup.setOnClickListener {
            viewModel.addThreeScheduleInDayDup()
            FancyToast.makeText(
                applicationContext,
                "4번케이스 추가",
                FancyToast.LENGTH_SHORT,
                FancyToast.SUCCESS,
                true
            )
                .show()
        }

        binding.addRandomScheduleBtn.setOnClickListener {
            val dialog = LoadingDialog(this)
            dialog.show()
            viewModel.addRandomSchedule()
            Handler(mainLooper).postDelayed(
                {
                    dialog.dismiss()
                }, 2000
            )
        }
        binding.addNormalScheduleInweekBtn.setOnClickListener {
            val dialog = LoadingDialog(this)
            dialog.show()
            viewModel.addRandomScheduleInWeek()
            Handler(mainLooper).postDelayed(
                {
                    dialog.dismiss()
                }, 2000
            )
        }
        binding.addDupScheduleBtn.setOnClickListener {
            val dialog = LoadingDialog(this)
            dialog.show()
            viewModel.addRandomDupSchedule()
            Handler(mainLooper).postDelayed(
                {
                    dialog.dismiss()
                }, 2000
            )

        }
        binding.backBtn.setOnClickListener {
            finish()
        }
        binding.resetBtn.setOnClickListener {
            GlobalScope.launch {
                database.clearAllTables()
            }
            FancyToast.makeText(
                this@AdministerActivity,
                "CLEAR DB",
                FancyToast.LENGTH_SHORT,
                FancyToast.SUCCESS,
                true
            )
                .show()
        }
    }

    private fun setupObserveData() {
        viewModel.startCalendar.observe(this, {
            startDatePickerDialog.updateDate(
                it.get(Calendar.YEAR),
                it.get(Calendar.MONTH),
                it.get(Calendar.DAY_OF_MONTH)
            )
            viewModel.updateStartDateString()
        })

        viewModel.startDateString.observe(this, {
            binding.startdateText.text = it
        })
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

}