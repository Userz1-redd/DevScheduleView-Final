package com.example.memberscheduledev.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "user", indices = [Index(value = ["username"], unique = true)])
data class User(
    @ColumnInfo(name = "username") var name: String = ""
) {
    @PrimaryKey
    @ColumnInfo(name = "userid")
    var id: String = UUID.randomUUID().toString()
}