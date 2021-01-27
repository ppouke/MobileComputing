package com.example.myapplication.db

import androidx.room.Entity

import androidx.room.PrimaryKey

@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val reminder : String
)

