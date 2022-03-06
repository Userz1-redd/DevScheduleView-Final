package com.example.memberscheduledev.ui.scheduledetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.memberscheduledev.R
import com.example.memberscheduledev.ViewModelFactory
import com.example.memberscheduledev.data.source.SchedulesRepository
import com.example.memberscheduledev.data.source.local.SchedulesDatabase
import com.example.memberscheduledev.data.source.local.SchedulesLocalDataSource
import com.example.memberscheduledev.databinding.ActivityScheduleDetailBinding
import com.shashank.sony.fancytoastlib.FancyToast

class ScheduleDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleDetailBinding
    private val database by lazy {
        SchedulesDatabase.getDatabase(this)
    }
    private val repository by lazy {
        SchedulesRepository.getInstance(SchedulesLocalDataSource(database.schedulesDao()))
    }

    private val viewModel: ScheduleDetailViewModel by viewModels {
        ViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_detail)

        setupScheduleInfo()

        setupObserveDate()

        setupOnClickBtn()
    }

    private fun setupScheduleInfo() {
        val scheduleId = intent.getStringExtra("scheduleId")!!
        val userName = intent.getStringExtra("userName")!!
        viewModel.updateUserName(userName)
        viewModel.getScheduleInfo(scheduleId)
    }

    private fun setupObserveDate() {
        viewModel.userNameString.observe(this, {
            binding.usernameText.text = it
        })
        viewModel.scheduleNameString.observe(this, {
            binding.scheduleTitleText.text = it
        })
        viewModel.startDateString.observe(this, {
            binding.startdateText.text = it
        })
        viewModel.startTimeString.observe(this, {
            binding.starttimeText.text = it
        })
        viewModel.endDateString.observe(this, {
            binding.enddateText.text = it
        })
        viewModel.endTimeString.observe(this, {
            binding.endtimeText.text = it
        })
    }

    private fun setupOnClickBtn() {
        binding.deleteScheduleBtn.setOnClickListener {
            viewModel.deleteSchedule()
            FancyToast.makeText(
                applicationContext,
                "일정이 삭제되었습니다.",
                FancyToast.LENGTH_LONG,
                FancyToast.SUCCESS,
                true
            )
                .show()
            finish()
        }
    }

}