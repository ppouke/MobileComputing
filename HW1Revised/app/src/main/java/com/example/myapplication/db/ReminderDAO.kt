package com.example.myapplication.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ReminderDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminder(reminder: Reminder)

    @Query("SELECT * FROM reminder_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Reminder>>


    @Query("DELETE FROM reminder_table WHERE id = :delid")
    fun delete(delid: Int)


}