package com.example.memberscheduledev.ui.home

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.library.listener.OnClickScheduleListener
import com.example.library.listener.OnDateChangeListener
import com.example.library.listener.OnPageChangeListener
import com.example.library.listener.OnScheduleDropListener
import com.example.library.model.Schedule
import com.example.library.model.Time
import com.example.memberscheduledev.R
import com.example.memberscheduledev.ViewModelFactory
import com.example.memberscheduledev.data.source.SchedulesRepository
import com.example.memberscheduledev.data.source.local.SchedulesDatabase
import com.example.memberscheduledev.data.source.local.SchedulesLocalDataSource
import com.example.memberscheduledev.databinding.ActivityHomeBinding
import com.example.memberscheduledev.ui.administer.AdministerActivity
import com.example.memberscheduledev.ui.scheduleadd.ScheduleAddActivity
import com.example.memberscheduledev.ui.scheduledetail.ScheduleDetailActivity
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var datePickerDialog: DatePickerDialog
    private val database by lazy {
        SchedulesDatabase.getDatabase(this)
    }
    private val repository by lazy {
        SchedulesRepository.getInstance(SchedulesLocalDataSource(database.schedulesDao()))
    }

    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        setupCalendarAndPicker()

        setupNavigationDrawer()

        setupOnClickBtn()

        setupObserveData()

        setupDevListener()

    }

    private fun setupCalendarAndPicker() {
        datePickerDialog = DatePickerDialog(
            this, null, 0, 0, 0
        )
    }

    private fun setupObserveData() {

        viewModel.viewType.observe(this, {
            binding.devscheduleView.setViewType(it)
        })

        viewModel.calendar.observe(this, {
            binding.devscheduleView.setDate(
                Time(
                    it.get(Calendar.YEAR),
                    it.get(Calendar.MONTH) + 1, it.get(Calendar.DAY_OF_MONTH), 0, 0, ""
                )
            )
            viewModel.updateCurrentDateText(
                "${it.get(Calendar.YEAR)}.${it.get(Calendar.MONTH) + 1}.${it.get(Calendar.DAY_OF_MONTH)}"
            )
        })

        viewModel.currentDate.observe(this, {
            binding.currentDateText.text = it
        })

        viewModel.pagingUserList.observe(this, {
            binding.devscheduleView.setList(it)
        })
    }

    private fun setupNavigationDrawer() {

        binding.topAppBar.setNavigationOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_setting -> {
                    startActivity(Intent(this@HomeActivity, AdministerActivity::class.java))
                }
            }
            binding.drawerLayout.closeDrawer(Gravity.LEFT)
            true
        }
    }

    private fun setupOnClickBtn() {

        binding.addScheduleBtn.setOnClickListener {
            startActivity(Intent(this, ScheduleAddActivity::class.java))
        }

        binding.viewtypeRadiogroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.dayview_btn -> {
                    viewModel.updateViewType(0)
                }
                R.id.weekview_btn -> {
                    viewModel.updateViewType(1)
                }
            }
        }

        binding.currentDateText.setOnClickListener {
            val cal = viewModel.calendar.value!!
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, month)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    viewModel.updateCalendar(cal)
                }

            datePickerDialog = DatePickerDialog(
                this, dateSetListener, cal.get(Calendar.YEAR), cal.get(
                    Calendar.MONTH
                ), cal.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        binding.devscheduleView.setOnClickScheduleListener(object : OnClickScheduleListener {
            override fun onClickEmptySchedule() {
                startActivity(Intent(this@HomeActivity, ScheduleAddActivity::class.java))
            }

            override fun onClickSchedule(position: Int, scheduleidx: Int) {
                val intent = Intent(this@HomeActivity, ScheduleDetailActivity::class.java)
                intent.putExtra(
                    "scheduleId",
                    binding.devscheduleView.getUserList()[position].scheduleList[scheduleidx].id
                )
                intent.putExtra("userName", binding.devscheduleView.getUserList()[position].name)
                startActivity(intent)
            }
        })

        binding.devscheduleView.setOnDateChangeListener(object : OnDateChangeListener {
            override fun OnDateChange(date: Time) {
                viewModel.updateCurrentDateText("${date.year}.${date.month},${date.day}")
            }
        })
    }

    private fun setupDevListener() {
        binding.devscheduleView.setOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageChange(page: Int) {
                viewModel.loadUserListPaging(page)
            }
        })

        binding.devscheduleView.setOnScheduleDropListener(object : OnScheduleDropListener {
            override fun onScheduleDrop(changeUserId: String, schedule: Schedule) {
                viewModel.modifySchedule(changeUserId, schedule)
            }
        })
    }

    override fun onResume() {
        binding.devscheduleView.submitUserList(ArrayList())
        binding.devscheduleView.resetCurrentPage()
        super.onResume()
    }

    override fun onPause() {
        datePickerDialog.dismiss()
        super.onPause()
    }

}