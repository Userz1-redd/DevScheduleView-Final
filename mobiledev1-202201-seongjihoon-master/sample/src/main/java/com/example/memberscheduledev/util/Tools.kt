package com.example.memberscheduledev.util

import androidx.lifecycle.MutableLiveData

object Tools {
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.value.also { this.value = it }
    }

    fun convertDate(date: String): String {
        val splitString = date.split(".")
        val month = if (splitString[1].length == 1) "0${splitString[1]}" else splitString[1]
        val dayOfWeek = if (splitString[2].length == 1) "0${splitString[2]}" else splitString[2]
        val hour = if (splitString[3].length == 1) "0${splitString[3]}" else splitString[3]
        val minute = if (splitString[4].length == 1) "0${splitString[4]}" else splitString[4]
        return "${splitString[0]}.${month}.${dayOfWeek}.${hour}.${minute}"
    }
}