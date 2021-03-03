package com.example.myapplication.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "reminder_table")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val message : String,
    val location_x : String,
    val location_y : String,
    val reminder_time : Long,
    val creation_time : Long,
    val creator_id : String,
    val reminder_seen : Boolean,
    val URI : String

): Parcelable



