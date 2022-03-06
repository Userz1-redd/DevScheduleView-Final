package com.example.memberscheduledev

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.memberscheduledev.data.source.SchedulesRepository
import com.example.memberscheduledev.ui.administer.AdministerViewModel
import com.example.memberscheduledev.ui.home.HomeViewModel
import com.example.memberscheduledev.ui.scheduleadd.ScheduleAddViewModel
import com.example.memberscheduledev.ui.scheduledetail.ScheduleDetailViewModel

class ViewModelFactory(
    private val schedulesRepository: SchedulesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        with(modelClass) {
            when {
                isAssignableFrom(HomeViewModel::class.java) ->
                    HomeViewModel(schedulesRepository)
                isAssignableFrom(ScheduleAddViewModel::class.java) ->
                    ScheduleAddViewModel(schedulesRepository)
                isAssignableFrom(ScheduleDetailViewModel::class.java) ->
                    ScheduleDetailViewModel(schedulesRepository)
                isAssignableFrom(AdministerViewModel::class.java) ->
                    AdministerViewModel(schedulesRepository)
                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T
}