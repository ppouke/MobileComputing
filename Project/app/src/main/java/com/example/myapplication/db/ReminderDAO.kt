package com.example.myapplication.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.myapplication.model.Reminder

@Dao
interface ReminderDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addReminder(reminder: Reminder):Long

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Query("SELECT * FROM reminder_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Reminder>>

    @Query("SELECT * FROM reminder_table ORDER BY id DESC LIMIT 1")
    fun readLast(): LiveData<Reminder>


    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Query("DELETE FROM reminder_table ")
    suspend fun deleteAllReminders()


}