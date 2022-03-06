package com.example.library.model

data class User(
    var id : String,
    var name : String,
    var scheduleList : ArrayList<Schedule>
)